package uk.gov.hmcts.unspec.statemachine;

import com.google.common.collect.Sets;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.generated.enums.CaseState;
import org.jooq.generated.enums.Event;
import org.jooq.generated.tables.pojos.CaseHistory;
import org.jooq.generated.tables.records.CasesRecord;
import org.jooq.generated.tables.records.EventsRecord;
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
import uk.gov.hmcts.unspec.CaseHandlerImpl;
import uk.gov.hmcts.unspec.dto.Party;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.jooq.generated.Tables.CASES;
import static org.jooq.generated.Tables.CASE_HISTORY;
import static org.jooq.generated.Tables.EVENTS;
import static org.jooq.generated.Tables.PARTIES;
import static org.jooq.generated.Tables.PARTIES_WITH_CLAIMS;

@RestController
@RequestMapping("/web/cases")
public class CaseMachine {

    @Autowired
    CaseHandlerImpl stateMachineSupplier;

    @Autowired
    DefaultDSLContext jooq;


    @GetMapping(path = "/{caseId}")
    public CaseActions getCase(@PathVariable("caseId") Long caseId) {
        StateMachine<CaseState, Event, EventsRecord> statemachine = stateMachineSupplier.buildCase();
        statemachine.rehydrate(caseId);
        return new CaseActions(Long.valueOf(caseId), statemachine.getState(), statemachine.getAvailableActions(statemachine.getState()));
    }

    @GetMapping(path = "/{caseId}/events")
    public List<CaseHistory> getCaseEvents(@PathVariable("caseId") String caseId) {
        return jooq.select()
            .from(CASE_HISTORY)
            .where(CASE_HISTORY.CASE_ID.eq(Long.valueOf(caseId)))
            .orderBy(CASE_HISTORY.TIMESTAMP.desc())
            .fetchInto(CaseHistory.class);
    }

    @Data
    @AllArgsConstructor
    public static class CaseParty {
        Long partyId;
        Party data;
        PartyClaims claims;

        @NoArgsConstructor
        @Data
        public static class PartyClaims {
            List<Long> claimant;
            List<Long> defendant;
        }
    }

    @GetMapping(path = "/{caseId}/parties")
    public List<CaseParty> getParties(@PathVariable("caseId") String caseId) {
        return jooq.select(PARTIES.PARTY_ID, PARTIES.DATA, PARTIES_WITH_CLAIMS.CLAIMS)
                .from(PARTIES)
                .join(PARTIES_WITH_CLAIMS).using(PARTIES.PARTY_ID)
                .where(PARTIES.CASE_ID.eq(Long.valueOf(caseId)))
                .orderBy(PARTIES.CASE_ID.asc())
                .fetchInto(CaseParty.class);
    }

    public ResponseEntity<String> createEvent(@PathVariable("caseId") Long caseId,
                                              @RequestBody ApiEventCreation event,
                                              String userId) {
        StateMachine<CaseState, Event, EventsRecord> statemachine = getStatemachine(caseId);
        StateMachine.TransitionContext context = new StateMachine.TransitionContext(userId, caseId);
        statemachine.handleEvent(context, Event.valueOf(event.getId()), event.getData());
        insertEvent(Event.valueOf(event.getId()), caseId, statemachine.getState(), userId);
        return ResponseEntity.created(URI.create("/cases/" + caseId))
                .body("");
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class CaseActions {
        private long id;
        private CaseState state;
        private Set<Event> actions;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CaseActions> createCase(@RequestBody ApiEventCreation event,
                                                  @Parameter(hidden = true) @AuthenticationPrincipal OidcUser user) {
        return createCase(event, user.getSubject());
    }

    public ResponseEntity<CaseActions> createCase(ApiEventCreation event, String userId) {
        CasesRecord c = jooq.newRecord(CASES);
        c.store();
        StateMachine<CaseState, Event, EventsRecord> statemachine = stateMachineSupplier.buildCase();
        insertEvent(Event.CreateClaim, c.getCaseId(), statemachine.getState(), userId);

        statemachine.onCreated(userId, c.getCaseId(), event.getData());

        return ResponseEntity.created(URI.create("/cases/" + c.getCaseId()))
                .body(new CaseActions(c.getCaseId(), statemachine.getState(), Sets.newHashSet()));
    }

    private StateMachine getStatemachine(long state) {
        StateMachine<CaseState, Event, EventsRecord> result = stateMachineSupplier.buildCase();
        result.rehydrate(state);
        return result;
    }

    private void insertEvent(Event eventId, Long caseId, CaseState state, String userId) {
        jooq.insertInto(EVENTS)
            .columns(EVENTS.ID, EVENTS.CASE_ID, EVENTS.STATE,
                EVENTS.USER_ID)
            .values(eventId, caseId, state, userId)
            .execute();
    }
}
