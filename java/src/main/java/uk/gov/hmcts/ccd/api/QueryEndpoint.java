package uk.gov.hmcts.ccd.api;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ccf.controller.kase.CaseController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/aggregated",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@Hidden
public class QueryEndpoint {

    @Autowired
    CaseController caseController;

    @RequestMapping(value = "/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases",
        method = RequestMethod.GET)
    public SearchResultView searchNew(@PathVariable("jid") final String jurisdictionId,
                                      @PathVariable("ctid") final String caseTypeId,
                                      @RequestParam java.util.Map<String, String> params) {
        List<SearchResultViewColumn> columns = new ArrayList<>();
        columns.add(SearchResultViewColumn.builder()
            .caseFieldId("id")
            .label("Case reference")
            .caseFieldTypeDefinition(FieldTypeDefinition.builder()
                .type(FieldTypeDefinition.LABEL)
                .build()).build());

        columns.add(SearchResultViewColumn.builder()
            .caseFieldId("date")
            .label("Case created date")
            .caseFieldTypeDefinition(FieldTypeDefinition.builder()
                .type(FieldTypeDefinition.DATETIME)
                .build()).build());

        columns.add(SearchResultViewColumn.builder()
            .caseFieldId("party_count")
            .label("Number of parties")
            .caseFieldTypeDefinition(FieldTypeDefinition.builder()
                .type(FieldTypeDefinition.LABEL)
                .build()).build());

        List<SearchResultViewItem> items = new ArrayList<>();
        List<CaseController.CaseSearchResult> cases = caseController.searchCases("e30=");
        for (CaseController.CaseSearchResult c : cases) {
            String cid = String.valueOf(c.getCaseId()).replaceFirst("(\\d{4})(\\d{4})(\\d{4})(\\d{4})", "$1-$2-$3-$4");
            items.add(SearchResultViewItem.builder()
                .caseId(cid)
                .fields(Map.of("id", cid,
                    "date", LocalDateTime.now(),
                    "party_count", c.getPartyCount().toString()
                ))
                .build());
        }

        return new SearchResultView(columns, items, null);
    }

}
