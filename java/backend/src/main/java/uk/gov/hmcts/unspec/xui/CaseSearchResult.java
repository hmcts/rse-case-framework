package uk.gov.hmcts.unspec.xui;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.generated.enums.CaseState;
import uk.gov.hmcts.ccf.XUI;
import uk.gov.hmcts.ccf.XUISearchHandler;

@AllArgsConstructor
@Data
public class CaseSearchResult implements XUISearchHandler.XUISearchResult {
    @XUI(label = "Case ID")
    private Long caseId;
    @XUI(label = "Parent case ID")
    private Long parentCaseId;
    @XUI(label = "State")
    private CaseState state;
    @XUI(label = "Number of parties")
    private Long partyCount;
}
