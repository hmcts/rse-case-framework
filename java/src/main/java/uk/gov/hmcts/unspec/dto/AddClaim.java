package uk.gov.hmcts.unspec.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AddClaim {
    private Map<Long, Boolean> claimants;
    private Map<Long, Boolean> defendants;
}
