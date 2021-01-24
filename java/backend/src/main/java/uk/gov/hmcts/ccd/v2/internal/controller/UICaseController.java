package uk.gov.hmcts.ccd.v2.internal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ccd.v2.V2;
import uk.gov.hmcts.ccd.v2.internal.resource.CaseHistoryViewResource;
import uk.gov.hmcts.ccd.v2.internal.resource.CaseViewResource;

@RestController
@RequestMapping(path = "/internal/cases")
public class UICaseController {
    private static final String ERROR_CASE_ID_INVALID = "Case ID is not valid";

    @GetMapping(
        path = "/{caseId}",
        headers = {
            V2.EXPERIMENTAL_HEADER
        },
        produces = {
            V2.MediaType.UI_CASE_VIEW
        }
    )
    public ResponseEntity<CaseViewResource> getCaseView(@PathVariable("caseId") String caseId) {
        throw new RuntimeException();
    }

    @GetMapping(
        path = "/{caseId}/events/{eventId}",
        headers = {
            V2.EXPERIMENTAL_HEADER
        },
        produces = {
            V2.MediaType.UI_EVENT_VIEW
        }
    )
    public ResponseEntity<CaseHistoryViewResource> getCaseHistoryView(@PathVariable("caseId") String caseId,
                                                                             @PathVariable("eventId") String eventId) {
        throw new RuntimeException();
    }
}
