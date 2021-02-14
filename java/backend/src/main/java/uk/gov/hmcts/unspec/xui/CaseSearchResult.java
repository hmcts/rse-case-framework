package uk.gov.hmcts.unspec.xui;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.generated.enums.CaseState;
import uk.gov.hmcts.ccf.XUISearchHandler;

@AllArgsConstructor
@Data
public class CaseSearchResult implements XUISearchHandler.XUISearchResult {
    private Long caseId;
    private Long parentCaseId;
    private CaseState state;
    private Long partyCount;
}
