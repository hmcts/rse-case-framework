package uk.gov.hmcts.unspec.model;

import lombok.Data;
import uk.gov.hmcts.unspec.enums.ClaimState;

import java.util.List;

@Data
public class Claim {
    private List<Long> claimantIds;
    private List<Long> defendantIds;
    private ClaimState state = ClaimState.Issued;
    private long lowerValue;
    private long higherValue;
}
