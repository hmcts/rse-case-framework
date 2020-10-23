package uk.gov.hmcts.unspec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.ccf.Case;
import uk.gov.hmcts.ccf.CaseHandler;
import uk.gov.hmcts.ccf.StateMachine;
import uk.gov.hmcts.unspec.dto.AddClaim;
import uk.gov.hmcts.unspec.dto.Individual;
import uk.gov.hmcts.unspec.dto.Party;
import uk.gov.hmcts.unspec.enums.Event;
import uk.gov.hmcts.unspec.enums.State;
import uk.gov.hmcts.unspec.event.AddNotes;
import uk.gov.hmcts.unspec.event.CloseCase;
import uk.gov.hmcts.unspec.event.CreateClaim;
import uk.gov.hmcts.unspec.model.Claim;
import uk.gov.hmcts.unspec.model.UnspecCase;
import uk.gov.hmcts.unspec.repository.CaseRepository;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jooq.generated.Tables.UNSPEC_CASES;
import static org.jooq.generated.tables.Citizen.CITIZEN;

@Service
public class CaseHandlerImpl implements CaseHandler {

    @Autowired
    DefaultDSLContext jooq;

    @Autowired
    CaseRepository repository;

    @Override
    public JsonNode get(Long caseId) {
        UnspecCase c = repository.load(Long.valueOf(caseId));
        return new ObjectMapper().valueToTree(c);
    }

    @Override
    public Collection<Case> search(Map<String, String> params) {
        Object id = params.get("id");
        Condition condition = DSL.trueCondition();
        if (id != null && id.toString().length() > 0) {
            condition = condition.and(UNSPEC_CASES.CASE_ID.equal(Long.valueOf(id.toString())));
        }

        return jooq.select()
                .from(UNSPEC_CASES)
                .where(condition)
                .orderBy(UNSPEC_CASES.CASE_ID.asc())
                .fetch()
                .stream()
                // TODO
                .map(x -> new Case(x.get(UNSPEC_CASES.CASE_ID), null))
                .collect(Collectors.toUnmodifiableList());

    }

    public StateMachine<State, Event> build() {
        StateMachine<State, Event> result = new StateMachine<>();
        result.initialState(State.Created, this::onCreate)
                .addUniversalEvent(Event.AddNotes, this::addNotes)
                .addUniversalEvent(Event.PurgeInactiveCitizens, this::purgeInactive)
                .addFileUploadEvent(State.Created, Event.ImportCitizens, this::bulkImport)
                .addEvent(State.Created, Event.AddParty, this::addParty)
                .addEvent(State.Created, Event.AddClaim, this::addClaim)
                .addTransition(State.Created, State.Closed, Event.CloseCase, this::closeCase)
                .addTransition(State.Closed, State.Stayed, Event.SubmitAppeal, this::closeCase);
        return result;
    }

    private void purgeInactive(Long caseId, Object o) {
        jooq.delete(CITIZEN)
                .where(CITIZEN.STATUS.eq("Inactive"))
                .execute();
    }

    @SneakyThrows
    private void bulkImport(Long caseId, MultipartFile f) {
        try (InputStream is = f.getInputStream()) {
            CSVParser records = CSVFormat.DEFAULT.parse(new InputStreamReader(is));
            // Add the caseId column
            List<Object[]> rows = records.getRecords().stream().map(x -> {
                return new Object[]{caseId, x.get(0), x.get(1), x.get(2), x.get(3), x.get(4)};
            }).collect(Collectors.toList());
            jooq.loadInto(CITIZEN)
                    .loadArrays(rows)
                    .fields(CITIZEN.CASE_ID, CITIZEN.TITLE, CITIZEN.FORENAME, CITIZEN.SURNAME, CITIZEN.DATE_OF_BIRTH,
                            CITIZEN.STATUS)
                    .execute();
        }
    }

    private void addClaim(Long caseId, AddClaim claim) {
        List<Long> claimantIds = claim.getClaimants().entrySet().stream().filter((x) -> x.getValue())
                .map(x -> x.getKey())
                .collect(Collectors.toUnmodifiableList());
        if (claimantIds.size() == 0) {
            throw new IllegalArgumentException("Must have at least one defendant!");
        }

        List<Long> defendantIds = claim.getDefendants().entrySet().stream().filter((x) -> x.getValue())
                .map(x -> x.getKey())
                .collect(Collectors.toUnmodifiableList());
        if (defendantIds.size() == 0) {
            throw new IllegalArgumentException("Must have at least one claimant!");
        }

        Claim c = new Claim();
        c.setClaimantIds(claimantIds);
        c.setDefendantIds(defendantIds);
        c.setLowerValue(claim.getLowerValue());
        c.setHigherValue(claim.getHigherValue());

        UnspecCase cse = repository.load(caseId);
        cse.getClaims().add(c);
        repository.save(cse);
    }

    private void addParty(Long id, Party party) {
        UnspecCase c = repository.load(id);
        c.getParties().add(party);
        repository.save(c);
    }

    private void addNotes(Long id, AddNotes notes) {
        UnspecCase c = repository.load(id);
        c.getNotes().add(notes.getNotes());
        repository.save(c);
    }

    @SneakyThrows
    private void onCreate(Long id, CreateClaim request) {
        String ref = request.getClaimantReference();
        if (ref == null || ref.length() == 0 || ref.contains("@")) {
            throw new IllegalArgumentException("Invalid reference!");
        }

        if (request.getClaimant() instanceof Individual) {
            Individual i = (Individual) request.getClaimant();
            if (i.getTitle() == null || i.getTitle().length() == 0) {
                throw new RuntimeException();
            }
        }

        List<Party> parties = Lists.newArrayList(request.getClaimant(), request.getDefendant());
        UnspecCase data = new UnspecCase(id, parties);
        data.setCourtLocation(request.getApplicantPreferredCourt());

        repository.save(data);
    }

    private void closeCase(Long id, CloseCase t) {

    }
}
