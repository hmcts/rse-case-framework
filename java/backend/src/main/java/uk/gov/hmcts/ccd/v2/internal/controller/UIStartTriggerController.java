package uk.gov.hmcts.ccd.v2.internal.controller;

import org.jooq.generated.enums.CaseState;
import org.jooq.generated.enums.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseUpdateViewEvent;
import uk.gov.hmcts.ccd.v2.V2;
import uk.gov.hmcts.ccd.v2.internal.resource.CaseUpdateViewEventResource;
import uk.gov.hmcts.ccf.StateMachine;
import uk.gov.hmcts.unspec.CaseHandlerImpl;

@RestController
@RequestMapping(path = "/data/internal")
public class UIStartTriggerController {
    private static final String ERROR_CASE_ID_INVALID = "Case ID is not valid";

    @Autowired
    CaseHandlerImpl stateMachineSupplier;

    @GetMapping(
        path = "/case-types/{caseTypeId}/event-triggers/{triggerId}",
        headers = {
            V2.EXPERIMENTAL_HEADER
        },
        produces = {
            V2.MediaType.CASE_TYPE_UPDATE_VIEW_EVENT
        }
    )
    public ResponseEntity<CaseUpdateViewEventResource> getCaseUpdateViewEventByCaseType(@PathVariable("caseTypeId")
                                                                                                String caseTypeId,
                                                                                        @PathVariable("triggerId")
                                                                                            String triggerId,
                                                               @RequestParam(value = "ignore-warning", required = false)
                                                                                          final Boolean ignoreWarning) {

        throw new RuntimeException();
    }

    @GetMapping(
        path = "/cases/{caseId}/event-triggers/{triggerId}",
        headers = {
            V2.EXPERIMENTAL_HEADER
        },
        produces = {
            V2.MediaType.CASE_UPDATE_VIEW_EVENT
        }
    )
    public ResponseEntity<CaseUpdateViewEventResource> getCaseUpdateViewEvent(@PathVariable("caseId") String caseId,
                                                                            @PathVariable("triggerId") String triggerId,
                                                                            @RequestParam(value = "ignore-warning",
                                                                                required = false)
                                                                                  final Boolean ignoreWarning) {
        StateMachine<CaseState, Event> s =
            stateMachineSupplier.build();
        CaseUpdateViewEvent view = s.getEvent(Long.valueOf(caseId), Event.valueOf(triggerId));
        CaseUpdateViewEventResource e = CaseUpdateViewEventResource.forCase(
            view,
            caseId,
            ignoreWarning);
        e.getCaseUpdateViewEvent().setCaseId(caseId);
        return ResponseEntity.ok(e);
    }

    @GetMapping(
        path = "/drafts/{draftId}/event-trigger",
        headers = {
            V2.EXPERIMENTAL_HEADER
        },
        produces = {
            V2.MediaType.UI_START_DRAFT_TRIGGER
        }
    )
    public ResponseEntity<CaseUpdateViewEventResource> getStartDraftTrigger(@PathVariable("draftId") String draftId,
                                                                            @RequestParam(value = "ignore-warning",
                                                                                required = false)
                                                                            final Boolean ignoreWarning) {

        throw new RuntimeException();
    }
}