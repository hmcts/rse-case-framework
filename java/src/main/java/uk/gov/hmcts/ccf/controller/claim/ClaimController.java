package uk.gov.hmcts.ccf.controller.claim;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.Record2;
import org.jooq.generated.enums.ClaimEvent;
import org.jooq.generated.enums.ClaimState;
import org.jooq.generated.tables.pojos.ClaimHistory;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ccf.StateMachine;
import uk.gov.hmcts.ccf.controller.kase.ApiEventCreation;
import uk.gov.hmcts.unspec.dto.ConfirmService;
import uk.gov.hmcts.unspec.dto.Party;
import uk.gov.hmcts.unspec.event.CreateClaim;

import java.net.URI;
import java.util.List;

import static org.jooq.generated.Tables.CLAIMS_WITH_PARTIES;
import static org.jooq.generated.Tables.CLAIMS_WITH_STATES;
import static org.jooq.generated.Tables.CLAIM_EVENTS;
import static org.jooq.generated.Tables.CLAIM_HISTORY;

@RestController
@RequestMapping("/web")
public class ClaimController {

    @Autowired
    DefaultDSLContext jooq;

    @Data
    @NoArgsConstructor
    public static class Claim {
        Long claimId;
        Long caseId;
        Long lowerAmount;
        Long higherAmount;
        ClaimState state;
        ClaimParties parties;
    }

    @GetMapping(path = "/cases/{caseId}/claims")
    public List<Claim> getClaims(@PathVariable("caseId") Long caseId) {
        return jooq.select()
            .from(CLAIMS_WITH_STATES)
            .join(CLAIMS_WITH_PARTIES).using(CLAIMS_WITH_STATES.CLAIM_ID)
            .where(CLAIMS_WITH_STATES.CASE_ID.eq(caseId))
            .orderBy(CLAIMS_WITH_STATES.CLAIM_ID.asc())
            .fetchInto(Claim.class);
    }

    @GetMapping(path = "/claims/{claimId}/events")
    public List<ClaimHistory> getClaimEvents(@PathVariable("claimId") Long claimId) {
        return jooq.select()
            .from(CLAIM_HISTORY)
            .where(CLAIM_HISTORY.CLAIM_ID.eq(claimId))
            .orderBy(CLAIM_HISTORY.TIMESTAMP.desc())
            .fetch()
            .into(ClaimHistory.class);
    }

    @PostMapping(path = "/claims/{claimId}/events")
    @Transactional
    public ResponseEntity<String> createEvent(@PathVariable("claimId") Long claimId,
                                              @RequestBody ApiEventCreation event,
                                              @AuthenticationPrincipal OidcUser user) {
        return createEvent(claimId, event, user.getSubject());
    }

    public ResponseEntity<String> createEvent(Long claimId,
                                              ApiEventCreation event,
                                              String userId) {
        Record2<Integer, ClaimState> record = jooq.select(CLAIM_EVENTS.SEQUENCE_NUMBER, CLAIM_EVENTS.STATE)
            .from(CLAIM_EVENTS)
            .where(CLAIM_EVENTS.CLAIM_ID.eq(claimId))
            .orderBy(CLAIM_EVENTS.SEQUENCE_NUMBER.desc())
            .limit(1)
            .fetchSingle();

        StateMachine<ClaimState, ClaimEvent> statemachine = build(record.component2());
        StateMachine.TransitionContext context = new StateMachine.TransitionContext(userId, claimId);
        statemachine.handleEvent(context, ClaimEvent.valueOf(event.getId()), event.getData());
        return ResponseEntity.created(URI.create("/claims/" + claimId))
            .body("");
    }

    public StateMachine<ClaimState, ClaimEvent> build(ClaimState state) {
        StateMachine<ClaimState, ClaimEvent> result = new StateMachine<>();
        result.initialState(ClaimState.Issued, this::onCreate)
            .addTransition(ClaimState.Issued, ClaimState.ServiceConfirmed, ClaimEvent.ConfirmService,
                this::confirmService);
        result.rehydrate(state);
        return result;
    }

    public void onCreate(StateMachine.TransitionContext transitionContext, CreateClaim c) {

    }

    public void confirmService(StateMachine.TransitionContext context, ConfirmService service) {
        jooq.insertInto(CLAIM_EVENTS, CLAIM_EVENTS.CLAIM_ID, CLAIM_EVENTS.ID, CLAIM_EVENTS.STATE, CLAIM_EVENTS.USER_ID)
            .values(context.getEntityId(), ClaimEvent.ConfirmService, ClaimState.ServiceConfirmed, context.getUserId())
            .execute();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClaimParties {
        List<Party> claimants;
        List<Party> defendants;
    }
}
