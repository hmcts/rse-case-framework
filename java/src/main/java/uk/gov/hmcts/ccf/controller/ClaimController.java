package uk.gov.hmcts.ccf.controller;

import org.jooq.JSONFormat;
import org.jooq.Record2;
import org.jooq.generated.enums.ClaimEvent;
import org.jooq.generated.enums.ClaimState;
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
import uk.gov.hmcts.ccf.TransitionContext;
import uk.gov.hmcts.ccf.api.ApiEventCreation;
import uk.gov.hmcts.ccf.api.ApiEventHistory;
import uk.gov.hmcts.unspec.dto.ConfirmService;
import uk.gov.hmcts.unspec.event.CreateClaim;

import java.net.URI;
import java.util.List;

import static org.jooq.generated.Tables.CLAIMS_WITH_PARTIES;
import static org.jooq.generated.Tables.CLAIMS_WITH_STATES;
import static org.jooq.generated.Tables.CLAIM_EVENTS;
import static org.jooq.generated.Tables.USERS;

@RestController
@RequestMapping("/web")
public class ClaimController {

    @Autowired
    DefaultDSLContext jooq;

    @GetMapping(path = "/cases/{caseId}/claims")
    public String getClaims(@PathVariable("caseId") Long caseId) {
        return jooq.select()
            .from(CLAIMS_WITH_STATES)
            .join(CLAIMS_WITH_PARTIES).using(CLAIMS_WITH_STATES.CLAIM_ID)
            .where(CLAIMS_WITH_STATES.CASE_ID.eq(caseId))
            .orderBy(CLAIMS_WITH_STATES.CLAIM_ID.desc())
            .fetch()
            .formatJSON(JSONFormat.DEFAULT_FOR_RECORDS.recordFormat(JSONFormat.RecordFormat.OBJECT)
                .wrapSingleColumnRecords(false));
    }

    @GetMapping(path = "/claims/{claimId}/events")
    public List<ApiEventHistory> getClaimEvents(@PathVariable("claimId") Long claimId) {
        List<ApiEventHistory> result = jooq.select()
            .from(CLAIM_EVENTS)
            .join(USERS).using(USERS.USER_ID)
            .where(CLAIM_EVENTS.CLAIM_ID.eq(claimId))
            .orderBy(CLAIM_EVENTS.TIMESTAMP.desc())
            .fetch()
            .into(ApiEventHistory.class);

        return result;
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
        TransitionContext context = new TransitionContext(userId, claimId);
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

    public void onCreate(TransitionContext transitionContext, CreateClaim c) {

    }

    public void confirmService(TransitionContext context, ConfirmService service) {
        jooq.insertInto(CLAIM_EVENTS, CLAIM_EVENTS.CLAIM_ID, CLAIM_EVENTS.ID, CLAIM_EVENTS.STATE, CLAIM_EVENTS.USER_ID)
            .values(context.getEntityId(), ClaimEvent.ConfirmService, ClaimState.ServiceConfirmed, context.getUserId())
            .execute();
    }

    private void insertEvent(ClaimEvent eventId, Long claimId, ClaimState state, int sequence, String userId) {
        jooq.insertInto(CLAIM_EVENTS)
            .columns(CLAIM_EVENTS.ID, CLAIM_EVENTS.CLAIM_ID, CLAIM_EVENTS.STATE,
                CLAIM_EVENTS.USER_ID)
            .values(eventId, claimId, state, userId)
            .execute();
    }
}
