package uk.gov.hmcts.ccf.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.generated.enums.CaseState;

@AllArgsConstructor
@Data
class CaseSearchResult {
    private Long caseId;
    private CaseState state;
    private Long partyCount;
//    public CaseSearchResult(Long caseId, CaseState state, Long partyCount) {
//
//    }
}
