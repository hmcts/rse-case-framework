package uk.gov.hmcts.ccd.v2.external.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.jooq.generated.enums.CaseState;
import org.jooq.generated.enums.Event;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ccd.domain.model.std.CaseDataContent;
import uk.gov.hmcts.ccd.v2.V2;
import uk.gov.hmcts.ccd.v2.external.resource.CaseResource;
import uk.gov.hmcts.ccf.StateMachine;
import uk.gov.hmcts.unspec.CaseHandlerImpl;

import static org.jooq.generated.Tables.CASES_WITH_STATES;
import static org.jooq.generated.Tables.EVENTS;
import static org.springframework.http.ResponseEntity.status;

@RestController(value = "CCDController")
@RequestMapping(path = "/data")
public class CaseController {

    @Autowired
    CaseHandlerImpl stateMachineSupplier;

    @Autowired
    DefaultDSLContext jooq;

    @GetMapping(
        path = "/cases/{caseId}",
        headers = {
            V2.EXPERIMENTAL_HEADER
        },
        produces = {
            V2.MediaType.CASE
        }
    )
    public ResponseEntity<CaseResource> getCase(@PathVariable("caseId") String caseId) {
        throw new RuntimeException();
    }

    @SneakyThrows
    @PostMapping(
        path = "/cases/{caseId}/events"
    )
    @ResponseStatus(HttpStatus.CREATED) // To remove default 200 response from Swagger
    @SuppressWarnings("unchecked")
    public ResponseEntity<CaseResource> createEvent(@PathVariable("caseId") String caseId,
                                                    @RequestBody final CaseDataContent content) {
        StateMachine<CaseState, Event> statemachine = stateMachineSupplier.build();

        StateMachine.TransitionContext context = new StateMachine.TransitionContext(
            "a62f4e6f-c223-467d-acc1-fe91444783f5", Long.valueOf(caseId));
        Event event = Event.valueOf(content.getEventId());

        String json = new ObjectMapper().writeValueAsString(content.getData());
        JsonNode node = new ObjectMapper().readTree(json);

        CaseState state = jooq.select(CASES_WITH_STATES.STATE)
            .from(CASES_WITH_STATES)
            .where(CASES_WITH_STATES.CASE_ID.eq(Long.valueOf(caseId)))
            .fetchOne().value1();
        statemachine.rehydrate(state);

        statemachine.handleEvent(context, event, node);

        insertEvent(Event.valueOf(content.getEventId()), Long.valueOf(caseId), statemachine.getState(),
            "a62f4e6f-c223-467d-acc1-fe91444783f5"); // TODO

        CaseResource result = CaseResource.builder()
            .data(content.getData())
            .reference(content.getCaseReference())
            .jurisdiction("NFD")
            .state(state.getLiteral())
            .build();
        return status(HttpStatus.CREATED).body(result);
    }

    private void insertEvent(Event eventId, Long caseId, CaseState state, String userId) {
        jooq.insertInto(EVENTS)
            .columns(EVENTS.ID, EVENTS.CASE_ID, EVENTS.STATE,
                EVENTS.USER_ID)
            .values(eventId, caseId, state, userId)
            .execute();
    }


    @PostMapping(
        path = "/case-types/{caseTypeId}/cases",
        headers = {
            V2.EXPERIMENTAL_HEADER
        },
        produces = {
            V2.MediaType.CREATE_CASE
        }
    )
    public ResponseEntity<CaseResource> createCase(@PathVariable("caseTypeId") String caseTypeId,
                                                   @RequestBody final CaseDataContent content) {
        throw new RuntimeException();
    }


}
