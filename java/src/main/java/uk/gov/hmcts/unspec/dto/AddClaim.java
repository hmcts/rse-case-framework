package uk.gov.hmcts.unspec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddClaim {
    private Map<Long, Boolean> claimants;
    private Map<Long, Boolean> defendants;
    private long lowerValue;
    private long higherValue;
}
