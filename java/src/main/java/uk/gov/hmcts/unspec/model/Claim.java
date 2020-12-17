package uk.gov.hmcts.unspec.model;

import lombok.Data;
import org.jooq.generated.enums.ClaimState;

@Data
public class Claim {
    private ClaimState state = ClaimState.Issued;
    private long lowerValue;
    private long higherValue;
}
