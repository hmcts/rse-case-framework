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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.ccf.Case;
import uk.gov.hmcts.ccf.CaseHandler;
import uk.gov.hmcts.ccf.api.ApiEventCreation;
import uk.gov.hmcts.ccf.StateMachine;
import uk.gov.hmcts.unspec.StatemachineConfig;
import uk.gov.hmcts.ccf.api.ApiCase;
import uk.gov.hmcts.ccf.api.ApiEventHistory;
import uk.gov.hmcts.unspec.enums.Event;
import uk.gov.hmcts.unspec.enums.State;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.generated.Tables.CASES;
import static org.jooq.generated.Tables.EVENTS;
import static org.jooq.impl.DSL.count;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200", exposedHeaders = {"Location"})
public class WebController {

    @Autowired
    StatemachineConfig stateMachineSupplier;

    @Autowired
    CaseHandler caseHandler;

    @Autowired
    DefaultDSLContext create;

    @PostMapping(path = "/cases")
    @Transactional
    public ResponseEntity<ApiCase> createCase(@RequestBody ApiEventCreation event) {
        CasesRecord c = create.newRecord(CASES);
        c.setDescription("Never going to happen");
        c.store();
        StateMachine<State, Event> statemachine = stateMachineSupplier.build();
        create.insertInto(EVENTS)
            .columns(EVENTS.ID, EVENTS.CASE_ID, EVENTS.STATE, EVENTS.SEQUENCE_NUMBER, EVENTS.TIMESTAMP, EVENTS.USER_FORENAME, EVENTS.USER_SURNAME)
            .values(Event.CreateClaim.toString(), c.getCaseId(), statemachine.getState().toString(), 1, LocalDateTime.now(), "Alex", "M")
            .execute();

        StateMachine<State, Event> statemachine2 = stateMachineSupplier.build();
        statemachine.onCreated(c.getCaseId(), event.getData());

        return ResponseEntity.created(URI.create("/cases/" + c.getCaseId()))
                .body(new ApiCase(c.getCaseId(), statemachine.getState().toString(), Sets.newHashSet(), null));
    }

    @SneakyThrows
    @GetMapping( path = "/search")
    public Collection<ApiCase> searchCases(@RequestHeader("search-query") String base64JSONQuery) {
        byte[] bytes = Base64.getDecoder().decode(base64JSONQuery.getBytes());
        Map<String, String> query = new ObjectMapper().readValue(bytes, HashMap.class);

        Collection<Case> cases = caseHandler.search(query);
        return cases.stream().map(x -> getCase(x.getId())).collect(Collectors.toUnmodifiableList());
    }

    @GetMapping( path = "/cases/{caseId}")
    public ApiCase getCase(@PathVariable("caseId") Long caseId) {
        Record result = create.select(EVENTS.STATE)
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

    @GetMapping( path = "/cases/{caseId}/events")
    public List<ApiEventHistory> getCaseEvents(@PathVariable("caseId") Long caseId) {
        List<ApiEventHistory> result = create.select()
            .from(EVENTS)
            .where(EVENTS.CASE_ID.eq(caseId))
            .orderBy(EVENTS.SEQUENCE_NUMBER.desc())
            .fetch()
            .into(ApiEventHistory.class);

        return result;
    }

    @PostMapping( path = "/cases/{caseId}/events")
    @Transactional
    public ResponseEntity<String> createEvent(@PathVariable("caseId") Long caseId,
                                              @RequestBody ApiEventCreation event) {
        Record2<Integer, String> record = create.select(EVENTS.SEQUENCE_NUMBER, EVENTS.STATE)
                .from(EVENTS)
                .where(EVENTS.CASE_ID.eq(caseId))
                .orderBy(EVENTS.SEQUENCE_NUMBER.desc())
                .limit(1)
                .fetchSingle();

        StateMachine<State, Event> statemachine = getStatemachine(record.component2());
        statemachine.handleEvent(caseId, Event.valueOf(event.getId().toString()), event.getData());
        create.insertInto(EVENTS)
            .columns(EVENTS.ID, EVENTS.CASE_ID, EVENTS.STATE, EVENTS.SEQUENCE_NUMBER, EVENTS.TIMESTAMP, EVENTS.USER_FORENAME, EVENTS.USER_SURNAME)
            .values(event.getId(), caseId, statemachine.getState().toString(), record.value1() + 1, LocalDateTime.now(), "Alex", "M")
            .execute();
        return ResponseEntity.created(URI.create("/cases/" + caseId))
                .body("");
    }

    @PostMapping( path = "/cases/{caseId}/files")
    @Transactional
    public ResponseEntity<String> fileUpload(@PathVariable("caseId") Long caseId,
                                             @RequestParam("eventId") String eventId,
                                             @RequestParam("file") MultipartFile file) {
        Record2<Integer, String> record = create.select(EVENTS.SEQUENCE_NUMBER, EVENTS.STATE)
                .from(EVENTS)
                .where(EVENTS.CASE_ID.eq(caseId))
                .orderBy(EVENTS.SEQUENCE_NUMBER.desc())
                .limit(1)
                .fetchSingle();

        StateMachine<State, Event> statemachine = getStatemachine(record.component2());
        statemachine.handleFileUpload(record.component2(), caseId, Event.valueOf(eventId), file);
        create.insertInto(EVENTS)
                .columns(EVENTS.ID, EVENTS.CASE_ID, EVENTS.STATE, EVENTS.SEQUENCE_NUMBER, EVENTS.TIMESTAMP, EVENTS.USER_FORENAME, EVENTS.USER_SURNAME)
                .values(eventId, caseId, statemachine.getState().toString(), record.value1() + 1, LocalDateTime.now(), "Alex", "M")
                .execute();
        return ResponseEntity.created(URI.create("/cases/" + caseId))
                .body("");
    }

    @GetMapping( path = "/case_count")
    public int caseCount() {
        return create.select(count()).from(EVENTS).fetchSingle().value1();
    }

    private StateMachine getStatemachine(String state) {
        StateMachine<State, Event> result = stateMachineSupplier.build();
        result.rehydrate(state);
        return result;
    }
}
