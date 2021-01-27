package uk.gov.hmcts.ccd.v2.internal.controller;

import lombok.extern.slf4j.Slf4j;
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
import uk.gov.hmcts.ccf.ESQueryParser;
import uk.gov.hmcts.ccf.controller.kase.CaseController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jooq.generated.Tables.CASES_WITH_STATES;
import static org.jooq.generated.Tables.PARTIES;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.table;

@RestController
@RequestMapping(path = "/data/internal/searchCases", consumes = MediaType.APPLICATION_JSON_VALUE,
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

        ESQueryParser.ESQuery query = ESQueryParser.parse(jsonSearchRequest);

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
                .offset(query.getFrom())
                .limit(query.getPageSize())
                .fetchInto(CaseController.CaseSearchResult.class);

        List<SearchResultViewItem> cases = new ArrayList<>();
        for (CaseController.CaseSearchResult result : results) {
            cases.add(SearchResultViewItem.builder()
                .caseId(result.getCaseId().toString())
                .fields(Map.of(
                    "party_count", result.getPartyCount(),
                    "id", result.getCaseId(),
                    "parent_id", result.getParentCaseId() != null ? result.getParentCaseId() : "",
                    "state", result.getState()
                    ))
                .build());
        }
        return ResponseEntity.ok(CaseSearchResultViewResource.builder()
            .cases(cases)
            .total(110000L)
            .header(SearchResultViewHeaderGroup.builder()
                .metadata(HeaderGroupMetadata.builder()
                    .jurisdiction("DIVORCE")
                    .caseTypeId("NFD")
                    .build())
                .field(SearchResultViewHeader.builder()
                    .caseFieldId("id")
                    .label("Case ID")
                    .caseFieldTypeDefinition(FieldTypeDefinition.builder()
                        .id("Number")
                        .type("Number")
                        .build())
                    .build())
                .field(SearchResultViewHeader.builder()
                    .caseFieldId("state")
                    .label("State")
                    .caseFieldTypeDefinition(FieldTypeDefinition.builder()
                        .id("Text")
                        .type("Text")
                        .build())
                    .build())
                .field(SearchResultViewHeader.builder()
                    .caseFieldId("parent_id")
                    .label("Parent Case")
                    .caseFieldTypeDefinition(FieldTypeDefinition.builder()
                        .id("Number")
                        .type("Number")
                        .build())
                    .build())
                .field(SearchResultViewHeader.builder()
                    .caseFieldId("party_count")
                    .label("Number of parties")
                    .caseFieldTypeDefinition(FieldTypeDefinition.builder()
                        .id("Number")
                        .type("Number")
                        .build())
                    .build())
                .build())
            .build());
    }
}
