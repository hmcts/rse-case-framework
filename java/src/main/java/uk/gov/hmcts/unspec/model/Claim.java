package uk.gov.hmcts.unspec.model;

import lombok.Data;
import uk.gov.hmcts.unspec.enums.ClaimState;

@Data
public class Claim {
    private ClaimState state = ClaimState.Issued;
    private long lowerValue;
    private long higherValue;
}
