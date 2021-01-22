package uk.gov.hmcts.ccd.v2.internal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ccd.domain.model.definition.FieldTypeDefinition;
import uk.gov.hmcts.ccd.domain.model.search.elasticsearch.HeaderGroupMetadata;
import uk.gov.hmcts.ccd.domain.model.search.elasticsearch.SearchResultViewHeader;
import uk.gov.hmcts.ccd.domain.model.search.elasticsearch.SearchResultViewHeaderGroup;
import uk.gov.hmcts.ccd.domain.model.search.elasticsearch.SearchResultViewItem;
import uk.gov.hmcts.ccd.v2.internal.resource.CaseSearchResultViewResource;
import uk.gov.hmcts.ccf.controller.kase.CaseController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.generated.Tables.CASES_WITH_STATES;
import static org.jooq.generated.Tables.PARTIES;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.table;

@RestController
@RequestMapping(path = "/internal/searchCases", consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UICaseSearchController {

    @Autowired
    DefaultDSLContext jooq;

    @PostMapping(path = "")
    public ResponseEntity<CaseSearchResultViewResource> searchCases(
                                     @RequestParam(value = "ctid") String caseTypeId,
                                     @RequestParam(value = "use_case", required = false) final String useCase,
                                     @RequestBody String jsonSearchRequest) {


        List<CaseController.CaseSearchResult> results =
            jooq.with("party_counts").as(
                select(PARTIES.CASE_ID, count().as("party_count"))
                    .from(PARTIES)
                    .groupBy(PARTIES.CASE_ID)
            )
                .select()
                .from(CASES_WITH_STATES)
                .join(table("party_counts")).using(CASES_WITH_STATES.CASE_ID)
                .orderBy(CASES_WITH_STATES.CASE_ID.asc())
                .fetchInto(CaseController.CaseSearchResult.class);

        List<SearchResultViewItem> cases = new ArrayList<>();
        for (CaseController.CaseSearchResult result : results) {
            cases.add(SearchResultViewItem.builder()
                .caseId(result.getCaseId().toString())
                .fields(Map.of("foo", 5))
                .build());
        }
        return ResponseEntity.ok(CaseSearchResultViewResource.builder()
            .cases(cases)
            .header(SearchResultViewHeaderGroup.builder()
                .metadata(HeaderGroupMetadata.builder()
                    .jurisdiction("DIVORCE")
                    .caseTypeId("NFD")
                    .build())
                .field(SearchResultViewHeader.builder()
                    .caseFieldId("foo")
                    .label("Foo")
                    .caseFieldTypeDefinition(FieldTypeDefinition.builder()
                        .id("Number")
                        .type("Number")
                        .build())
                    .build())
                .build())
            .build());
        }
}
