package uk.gov.hmcts.ccd.v2.internal.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ccd.v2.internal.resource.CaseSearchResultViewResource;

import java.time.Instant;

@RestController
@RequestMapping(path = "/internal/searchCases", consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UICaseSearchController {

    @PostMapping(path = "")
    public ResponseEntity<CaseSearchResultViewResource> searchCases(
                                     @RequestParam(value = "ctid") String caseTypeId,
                                     @RequestParam(value = "use_case", required = false) final String useCase,
                                     @RequestBody String jsonSearchRequest) {
        Instant start = Instant.now();
        throw new RuntimeException();
    }

}
