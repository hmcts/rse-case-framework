package uk.gov.hmcts.unspec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.jooq.JSONB;
import org.jooq.generated.enums.CaseState;
import org.jooq.generated.enums.ClaimEvent;
import org.jooq.generated.enums.ClaimState;
import org.jooq.generated.enums.Event;
import org.jooq.generated.enums.PartyRole;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ccf.StateMachine;
import uk.gov.hmcts.unspec.dto.AddClaim;
import uk.gov.hmcts.unspec.dto.AddParty;
import uk.gov.hmcts.unspec.dto.Individual;
import uk.gov.hmcts.unspec.event.CloseCase;
import uk.gov.hmcts.unspec.event.CreateClaim;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jooq.generated.Tables.CLAIMS;
import static org.jooq.generated.Tables.CLAIM_EVENTS;
import static org.jooq.generated.Tables.CLAIM_PARTIES;
import static org.jooq.generated.Tables.PARTIES;

@Service
public class CaseHandlerImpl {

    @Autowired
    DefaultDSLContext jooq;

    public StateMachine<CaseState, Event> build() {
        StateMachine<CaseState, Event> result = new StateMachine<>();
        result.initialState(CaseState.Created, this::onCreate);

        result.addEvent(CaseState.Created, Event.AddParty, this::addParty)
            .field(AddParty::getPartyType)
            .field(AddParty::getTitle)
            .field(AddParty::getFirstName)
            .field(AddParty::getLastName)
            .field(AddParty::getDateOfBirth);

        result.addEvent(CaseState.Created, Event.AddClaim, this::addClaim);

        result.addTransition(CaseState.Created, CaseState.Closed, Event.CloseCase, this::closeCase);
        result.addTransition(CaseState.Closed, CaseState.Stayed, Event.SubmitAppeal, this::closeCase);
        return result;
    }

    @SneakyThrows
    public void addClaim(StateMachine.TransitionContext context, AddClaim claim) {
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

        Long claimId = jooq.insertInto(CLAIMS, CLAIMS.CASE_ID, CLAIMS.LOWER_AMOUNT, CLAIMS.HIGHER_AMOUNT)
                .values(context.getEntityId(), claim.getLowerValue(), claim.getHigherValue())
                .returning(CLAIMS.CLAIM_ID)
                .fetchOne().getClaimId();

        jooq.insertInto(CLAIM_EVENTS, CLAIM_EVENTS.CLAIM_ID, CLAIM_EVENTS.ID, CLAIM_EVENTS.STATE, CLAIM_EVENTS.USER_ID)
            .values(claimId, ClaimEvent.ClaimIssued, ClaimState.Issued, context.getUserId())
            .execute();

        List<Object[]> claimParties = claimantIds.stream().map(x -> {
            return new Object[]{claimId, x, PartyRole.claimant};
        }).collect(Collectors.toList());

        claimParties.addAll(defendantIds.stream().map(x -> {
            return new Object[]{claimId, x, PartyRole.defendant};
        }).collect(Collectors.toList()));


        jooq.loadInto(CLAIM_PARTIES)
                .loadArrays(claimParties)
                .fields(CLAIM_PARTIES.CLAIM_ID, CLAIM_PARTIES.PARTY_ID, CLAIM_PARTIES.PARTY_TYPE)
                .execute();
    }

    @SneakyThrows
    private void addParty(StateMachine.TransitionContext context, AddParty party) {
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());
        jooq.insertInto(PARTIES, PARTIES.CASE_ID, PARTIES.DATA)
                .values(context.getEntityId(), JSONB.valueOf(mapper.writeValueAsString(party)))
                .execute();
    }

    @SneakyThrows
    private void onCreate(StateMachine.TransitionContext context, CreateClaim request) {
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

        List<Long> partyIds = jooq.insertInto(PARTIES, PARTIES.CASE_ID, PARTIES.DATA)
                .values(context.getEntityId(), JSONB.valueOf(
                    new ObjectMapper().writeValueAsString(request.getClaimant())))
                .values(context.getEntityId(), JSONB.valueOf(
                    new ObjectMapper().writeValueAsString(request.getDefendant())))
                .returningResult(PARTIES.PARTY_ID)
                .fetch()
                .getValues(PARTIES.PARTY_ID);

        addClaim(context, AddClaim.builder()
                .lowerValue(request.getLowerValue())
                .higherValue(request.getHigherValue())
                .claimants(Map.of(partyIds.get(0), true))
                .defendants(Map.of(partyIds.get(1), true))
                .build());

    }

    private void closeCase(StateMachine.TransitionContext context, CloseCase t) {

    }
}
