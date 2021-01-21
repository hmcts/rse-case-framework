package uk.gov.hmcts.ccd.api;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/data",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@Hidden
public class CaseDetailsEndpoint {
    @GetMapping(value = "/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/pagination_metadata")
    public PaginatedSearchMetadata searchCasesMetadataForCaseworkers(@PathVariable("uid") final String uid,
                                                                     @PathVariable("jid") final String jurisdictionId,
                                                                     @PathVariable("ctid") final String caseTypeId,
                                                                     @RequestParam Map<String, String>
                                                                         queryParameters) {
        return new PaginatedSearchMetadata(2, 1);
    }
}
