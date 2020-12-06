package uk.gov.hmcts.unspec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.jooq.Condition;
import org.jooq.JSONB;
import org.jooq.JSONFormat;
import org.jooq.generated.enums.PartyType;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ccf.CaseHandler;
import uk.gov.hmcts.ccf.StateMachine;
import uk.gov.hmcts.unspec.dto.AddClaim;
import uk.gov.hmcts.unspec.dto.ConfirmService;
import uk.gov.hmcts.unspec.dto.Individual;
import uk.gov.hmcts.unspec.dto.Party;
import uk.gov.hmcts.unspec.enums.ClaimState;
import uk.gov.hmcts.unspec.enums.Event;
import uk.gov.hmcts.unspec.enums.State;
import uk.gov.hmcts.unspec.event.AddNotes;
import uk.gov.hmcts.unspec.event.CloseCase;
import uk.gov.hmcts.unspec.event.CreateClaim;
import uk.gov.hmcts.unspec.model.Claim;
import uk.gov.hmcts.unspec.model.UnspecCase;
import uk.gov.hmcts.unspec.repository.CaseRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jooq.generated.Tables.CASES_WITH_STATES;
import static org.jooq.generated.Tables.CLAIMS;
import static org.jooq.generated.Tables.CLAIM_PARTIES;
import static org.jooq.generated.Tables.PARTIES;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.table;

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

    @SneakyThrows
    @Override
    public String search(Map<String, String> params) {
        Object id = params.get("id");
        Condition condition = DSL.trueCondition();
        if (id != null && id.toString().length() > 0) {
            condition = condition.and(CASES_WITH_STATES.CASE_ID.equal(Long.valueOf(id.toString())));
        }

        return jooq.with("party_counts").as(
                        select(PARTIES.CASE_ID, count().as("party_count"))
                        .from(PARTIES)
                        .groupBy(PARTIES.CASE_ID)
                )
                .select()
                .from(CASES_WITH_STATES)
                .join(table("party_counts")).using(CASES_WITH_STATES.CASE_ID)
                .where(condition)
                .orderBy(CASES_WITH_STATES.CASE_ID.asc())
                .fetch()
                .formatJSON(JSONFormat.DEFAULT_FOR_RECORDS.recordFormat(JSONFormat.RecordFormat.OBJECT));
    }

    public StateMachine<State, Event> build() {
        StateMachine<State, Event> result = new StateMachine<>();
        result.initialState(State.Created, this::onCreate)
                .addUniversalEvent(Event.AddNotes, this::addNotes)
                .addUniversalEvent(Event.ConfirmService, this::confirmService)
                .addEvent(State.Created, Event.AddParty, this::addParty)
                .addEvent(State.Created, Event.AddClaim, this::addClaim)
                .addTransition(State.Created, State.Closed, Event.CloseCase, this::closeCase)
                .addTransition(State.Closed, State.Stayed, Event.SubmitAppeal, this::closeCase);
        return result;
    }

    public void confirmService(Long caseId, ConfirmService service) {
        jooq.update(CLAIMS)
                .set(CLAIMS.STATE, ClaimState.ServiceConfirmed.toString())
                .where(CLAIMS.CLAIM_ID.eq(service.getClaimId()))
                .execute();
    }


    @SneakyThrows
    public void addClaim(Long caseId, AddClaim claim) {
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
        c.setLowerValue(claim.getLowerValue());
        c.setHigherValue(claim.getHigherValue());

        Long claimId = jooq.insertInto(CLAIMS, CLAIMS.CASE_ID, CLAIMS.STATE, CLAIMS.LOWER_AMOUNT, CLAIMS.HIGHER_AMOUNT)
                .values(caseId, ClaimState.Issued.toString(), claim.getLowerValue(), claim.getHigherValue())
                .returning(CLAIMS.CLAIM_ID)
                .fetchOne().getClaimId();

        List<Object[]> claimParties = claimantIds.stream().map(x -> {
            return new Object[]{claimId, x, PartyType.claimant};
        }).collect(Collectors.toList());

        claimParties.addAll(defendantIds.stream().map(x -> {
            return new Object[]{claimId, x, PartyType.defendant};
        }).collect(Collectors.toList()));


        jooq.loadInto(CLAIM_PARTIES)
                .loadArrays(claimParties)
                .fields(CLAIM_PARTIES.CLAIM_ID, CLAIM_PARTIES.PARTY_ID, CLAIM_PARTIES.PARTY_TYPE)
                .execute();
    }

    @SneakyThrows
    private void addParty(Long id, Party party) {
        jooq.insertInto(PARTIES, PARTIES.CASE_ID, PARTIES.DATA)
                .values(id, JSONB.valueOf(new ObjectMapper().writeValueAsString(party)))
                .execute();
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

        UnspecCase data = new UnspecCase(id);
        data.setCourtLocation(request.getApplicantPreferredCourt());

        repository.save(data);

        List<Long> partyIds = jooq.insertInto(PARTIES, PARTIES.CASE_ID, PARTIES.DATA)
                .values(id, JSONB.valueOf(new ObjectMapper().writeValueAsString(request.getClaimant())))
                .values(id, JSONB.valueOf(new ObjectMapper().writeValueAsString(request.getDefendant())))
                .returningResult(PARTIES.PARTY_ID)
                .fetch()
                .getValues(PARTIES.PARTY_ID);

        addClaim(id, AddClaim.builder()
                .lowerValue(request.getLowerValue())
                .higherValue(request.getHigherValue())
                .claimants(Map.of(partyIds.get(0), true))
                .defendants(Map.of(partyIds.get(1), true))
                .build());

    }

    private void closeCase(Long id, CloseCase t) {

    }
}
