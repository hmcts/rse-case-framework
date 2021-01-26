package uk.gov.hmcts.ccd.v2.external.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
import uk.gov.hmcts.ccf.EventBuilder;
import uk.gov.hmcts.ccf.config.EventConfig;

import static org.springframework.http.ResponseEntity.status;

@RestController(value = "CCDController")
@RequestMapping(path = "/data")
public class CaseController {

    @Autowired
    private EventConfig config;

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
        EventBuilder.CCFEvent e = config.getEvent(content.getEventId());
        String json = new ObjectMapper().writeValueAsString(content.getData());
        Object instance = new ObjectMapper().readValue(json, e.getClazz());
        e.getHandler().accept(Long.valueOf(caseId), instance);
        CaseResource result = CaseResource.builder()
            .data(content.getData())
            .reference(content.getCaseReference())
            .jurisdiction("NFD")
            .state("Open")
            .build();
        return status(HttpStatus.CREATED).body(result);
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
