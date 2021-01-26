package uk.gov.hmcts.ccd.v2.internal.controller;

import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseUpdateViewEvent;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldTypeDefinition;
import uk.gov.hmcts.ccd.domain.model.definition.WizardPage;
import uk.gov.hmcts.ccd.domain.model.definition.WizardPageField;
import uk.gov.hmcts.ccd.v2.V2;
import uk.gov.hmcts.ccd.v2.internal.resource.CaseUpdateViewEventResource;

@RestController
@RequestMapping(path = "/data/internal")
public class UIStartTriggerController {
    private static final String ERROR_CASE_ID_INVALID = "Case ID is not valid";

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

        CaseUpdateViewEvent event = CaseUpdateViewEvent.builder()
            .id(triggerId)
            .name("General referral")
            .description("General referral")
            .caseId(caseId)
            .caseField(CaseViewField.builder()
                .id("details")
                .label("Details yo")
                .fieldTypeDefinition(FieldTypeDefinition.builder()
                    .id("TextArea")
                    .type("TextArea")
                    .build())
                .build())
            .wizardPage(WizardPage.builder()
                .id("generalReferralgeneralreferral")
                .order(1)
                .wizardPageField(WizardPageField.builder()
                    .caseFieldId("details")
                    .order(1)
                    .pageColumnNumber(1)
                    .build())
                .build())
            .build();
        CaseUpdateViewEventResource e = CaseUpdateViewEventResource.forCase(event, caseId, ignoreWarning);
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
