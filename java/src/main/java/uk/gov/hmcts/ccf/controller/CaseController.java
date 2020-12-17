package uk.gov.hmcts.ccf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import org.jooq.Condition;
import org.jooq.generated.enums.CaseState;
import org.jooq.generated.enums.Event;
import org.jooq.generated.tables.records.CasesRecord;
import org.jooq.impl.DSL;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ccf.StateMachine;
import uk.gov.hmcts.ccf.TransitionContext;
import uk.gov.hmcts.ccf.api.ApiEventCreation;
import uk.gov.hmcts.ccf.api.ApiEventHistory;
import uk.gov.hmcts.ccf.api.CaseActions;
import uk.gov.hmcts.unspec.CaseHandlerImpl;

import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.generated.Tables.CASES;
import static org.jooq.generated.Tables.CASES_WITH_STATES;
import static org.jooq.generated.Tables.CASE_HISTORY;
import static org.jooq.generated.Tables.EVENTS;
import static org.jooq.generated.Tables.PARTIES;
import static org.jooq.generated.Tables.PARTIES_WITH_CLAIMS;
import static org.jooq.generated.Tables.USERS;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.table;

@RestController
@RequestMapping("/web")
public class CaseController {

    @Autowired
    CaseHandlerImpl stateMachineSupplier;

    @Autowired
    DefaultDSLContext jooq;

    @SneakyThrows
    @GetMapping(path = "/search")
    public List<CaseSearchResult> searchCases(@RequestHeader("search-query") String base64JsonQuery) {
        byte[] bytes = Base64.getDecoder().decode(base64JsonQuery.getBytes());
        Map<String, String> query = new ObjectMapper().readValue(bytes, HashMap.class);

        Object id = query.get("id");
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
            .fetchInto(CaseSearchResult.class);
    }

    @GetMapping(path = "/cases/{caseId}")
    public CaseActions getCase(@PathVariable("caseId") Long caseId) {
        CaseState currentState = jooq.select(CASES_WITH_STATES.STATE)
            .from(CASES_WITH_STATES)
            .where(CASES_WITH_STATES.CASE_ID.eq(Long.valueOf(caseId)))
            .fetchOne().value1();

        StateMachine<CaseState, Event> statemachine = stateMachineSupplier.build();
        return new CaseActions(caseId, currentState, statemachine.getAvailableActions(currentState));
    }

    @GetMapping(path = "/cases/{caseId}/events")
    public List<ApiEventHistory> getCaseEvents(@PathVariable("caseId") Long caseId) {
        List<ApiEventHistory> result = jooq.select()
            .from(CASE_HISTORY)
            .join(USERS).using(USERS.USER_ID)
            .where(CASE_HISTORY.CASE_ID.eq(caseId))
            .orderBy(CASE_HISTORY.TIMESTAMP.desc())
            .fetch()
            .into(ApiEventHistory.class);

        return result;
    }

    @GetMapping(path = "/cases/{caseId}/parties")
    public List<CaseParty> getParties(@PathVariable("caseId") Long caseId) {
        return jooq.select(PARTIES.PARTY_ID, PARTIES.DATA, PARTIES_WITH_CLAIMS.CLAIMS)
                .from(PARTIES)
                .join(PARTIES_WITH_CLAIMS).using(PARTIES.PARTY_ID)
                .where(PARTIES.CASE_ID.eq(caseId))
                .orderBy(PARTIES.CASE_ID.asc())
                .fetchInto(CaseParty.class);
    }

    @PostMapping(path = "/cases/{caseId}/events")
    @Transactional
    public ResponseEntity<String> createEvent(@PathVariable("caseId") Long caseId,
                                              @RequestBody ApiEventCreation event,
                                              @AuthenticationPrincipal OidcUser user) {
        return createEvent(caseId, event, user.getSubject());
    }

    public ResponseEntity<String> createEvent(@PathVariable("caseId") Long caseId,
                                              @RequestBody ApiEventCreation event,
                                              String userId) {
        CaseState state = jooq.select(CASES_WITH_STATES.STATE)
                .from(CASES_WITH_STATES)
                .where(CASES_WITH_STATES.CASE_ID.eq(caseId))
                .fetchOne().value1();

        StateMachine<CaseState, Event> statemachine = getStatemachine(state);
        TransitionContext context = new TransitionContext(userId, caseId);
        statemachine.handleEvent(context, Event.valueOf(event.getId()), event.getData());
        insertEvent(Event.valueOf(event.getId()), caseId, statemachine.getState(), userId);
        return ResponseEntity.created(URI.create("/cases/" + caseId))
                .body("");
    }

    @PostMapping(path = "/cases")
    @Transactional
    public ResponseEntity<CaseActions> createCase(@RequestBody ApiEventCreation event,
                                                  @AuthenticationPrincipal OidcUser user) {
        return createCase(event, user.getSubject());
    }

    public ResponseEntity<CaseActions> createCase(@RequestBody ApiEventCreation event, String userId) {
        CasesRecord c = jooq.newRecord(CASES);
        c.store();
        StateMachine<CaseState, Event> statemachine = stateMachineSupplier.build();
        insertEvent(Event.CreateClaim, c.getCaseId(), statemachine.getState(), userId);

        statemachine.onCreated(userId, c.getCaseId(), event.getData());

        return ResponseEntity.created(URI.create("/cases/" + c.getCaseId()))
                .body(new CaseActions(c.getCaseId(), statemachine.getState(), Sets.newHashSet()));
    }

    private StateMachine getStatemachine(CaseState state) {
        StateMachine<CaseState, Event> result = stateMachineSupplier.build();
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
