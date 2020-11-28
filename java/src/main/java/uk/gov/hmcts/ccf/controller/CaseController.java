package uk.gov.hmcts.ccf.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.generated.tables.records.CasesRecord;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.ccf.Case;
import uk.gov.hmcts.ccf.CaseHandler;
import uk.gov.hmcts.ccf.StateMachine;
import uk.gov.hmcts.ccf.api.ApiCase;
import uk.gov.hmcts.ccf.api.ApiEventCreation;
import uk.gov.hmcts.ccf.api.ApiEventHistory;
import uk.gov.hmcts.ccf.api.UserInfo;
import uk.gov.hmcts.unspec.CaseHandlerImpl;
import uk.gov.hmcts.unspec.enums.Event;
import uk.gov.hmcts.unspec.enums.State;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jooq.generated.Tables.CASES;
import static org.jooq.generated.Tables.EVENTS;

@RestController
@RequestMapping("/web")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CaseController {

    @Autowired
    CaseHandlerImpl stateMachineSupplier;

    @Autowired
    CaseHandler caseHandler;

    @Autowired
    DefaultDSLContext jooq;

    @GetMapping("/userInfo")
    public UserInfo getUserInfo(
            @AuthenticationPrincipal OidcUser principal) {
        return new UserInfo(principal.getName(),
                principal.getAuthorities().stream()
                        .map(x -> x.getAuthority())
                        .collect(Collectors.toSet()));
    }

    @SneakyThrows
    @GetMapping(path = "/search")
    public Collection<ApiCase> searchCases(@RequestHeader("search-query") String base64JsonQuery) {
        byte[] bytes = Base64.getDecoder().decode(base64JsonQuery.getBytes());
        Map<String, String> query = new ObjectMapper().readValue(bytes, HashMap.class);

        Collection<Case> cases = caseHandler.search(query);
        return cases.stream().map(x -> getCase(x.getId())).collect(Collectors.toUnmodifiableList());
    }

    @GetMapping(path = "/cases/{caseId}")
    public ApiCase getCase(@PathVariable("caseId") Long caseId) {
        Record result = jooq.select(EVENTS.STATE)
            .from(EVENTS)
            .where(EVENTS.CASE_ID.eq(Long.valueOf(caseId)))
            .orderBy(EVENTS.SEQUENCE_NUMBER.desc())
            .limit(1)
            .fetchSingle();

        JsonNode data = caseHandler.get(caseId);
        String state = result.get(EVENTS.STATE);
        StateMachine<State, Event> statemachine = stateMachineSupplier.build();
        return new ApiCase(caseId, state, statemachine.getAvailableActions(state), data);
    }

    @GetMapping(path = "/cases/{caseId}/events")
    public List<ApiEventHistory> getCaseEvents(@PathVariable("caseId") Long caseId) {
        List<ApiEventHistory> result = jooq.select()
            .from(EVENTS)
            .where(EVENTS.CASE_ID.eq(caseId))
            .orderBy(EVENTS.SEQUENCE_NUMBER.desc())
            .fetch()
            .into(ApiEventHistory.class);

        return result;
    }

    @PostMapping(path = "/cases/{caseId}/events")
    @Transactional
    public ResponseEntity<String> createEvent(@PathVariable("caseId") Long caseId,
                                              @RequestBody ApiEventCreation event,
                                              @AuthenticationPrincipal OidcUser user) {
        return createEvent(caseId, event, user.getGivenName(), user.getFamilyName());
    }

    public ResponseEntity<String> createEvent(@PathVariable("caseId") Long caseId,
                                              @RequestBody ApiEventCreation event,
                                              String user, String surname) {
        Record2<Integer, String> record = jooq.select(EVENTS.SEQUENCE_NUMBER, EVENTS.STATE)
                .from(EVENTS)
                .where(EVENTS.CASE_ID.eq(caseId))
                .orderBy(EVENTS.SEQUENCE_NUMBER.desc())
                .limit(1)
                .fetchSingle();

        StateMachine<State, Event> statemachine = getStatemachine(record.component2());
        statemachine.handleEvent(caseId, Event.valueOf(event.getId().toString()), event.getData());
        insertEvent(event.getId(), caseId, statemachine.getState(), record.value1() + 1, user, surname);
        return ResponseEntity.created(URI.create("/cases/" + caseId))
                .body("");
    }

    @PostMapping(path = "/cases/{caseId}/files")
    @Transactional
    public ResponseEntity<String> fileUpload(@PathVariable("caseId") Long caseId,
                                             @RequestParam("eventId") String eventId,
                                             @RequestParam("file") MultipartFile file,
                                             @AuthenticationPrincipal OidcUser user) {
        Record2<Integer, String> record = jooq.select(EVENTS.SEQUENCE_NUMBER, EVENTS.STATE)
                .from(EVENTS)
                .where(EVENTS.CASE_ID.eq(caseId))
                .orderBy(EVENTS.SEQUENCE_NUMBER.desc())
                .limit(1)
                .fetchSingle();

        StateMachine<State, Event> statemachine = getStatemachine(record.component2());
        statemachine.handleFileUpload(record.component2(), caseId, Event.valueOf(eventId), file);
        insertEvent(eventId, caseId, statemachine.getState(), record.value1() + 1, user.getGivenName(),
                user.getFamilyName());
        return ResponseEntity.created(URI.create("/cases/" + caseId))
                .body("");
    }

    @PostMapping(path = "/cases")
    @Transactional
    public ResponseEntity<ApiCase> createCase(@RequestBody ApiEventCreation event,
                                              @AuthenticationPrincipal OidcUser user) {
        return createCase(event, user.getGivenName(), user.getFamilyName());
    }

    public ResponseEntity<ApiCase> createCase(@RequestBody ApiEventCreation event, String user, String surname) {
        CasesRecord c = jooq.newRecord(CASES);
        c.store();
        StateMachine<State, Event> statemachine = stateMachineSupplier.build();
        insertEvent(Event.CreateClaim.toString(), c.getCaseId(), statemachine.getState(), 1, user, surname);

        statemachine.onCreated(c.getCaseId(), event.getData());

        return ResponseEntity.created(URI.create("/cases/" + c.getCaseId()))
                .body(new ApiCase(c.getCaseId(), statemachine.getState().toString(), Sets.newHashSet(), null));
    }


    private StateMachine getStatemachine(String state) {
        StateMachine<State, Event> result = stateMachineSupplier.build();
        result.rehydrate(state);
        return result;
    }

    private void insertEvent(String eventId, Long caseId, State state, int sequence, String forename, String surname) {
        jooq.insertInto(EVENTS)
            .columns(EVENTS.ID, EVENTS.CASE_ID, EVENTS.STATE, EVENTS.SEQUENCE_NUMBER, EVENTS.TIMESTAMP,
                EVENTS.USER_FORENAME, EVENTS.USER_SURNAME)
            .values(eventId, caseId, state.toString(), sequence,
                LocalDateTime.now(), forename, surname)
            .execute();
    }
}
