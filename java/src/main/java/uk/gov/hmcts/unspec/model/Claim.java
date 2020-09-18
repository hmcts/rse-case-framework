package uk.gov.hmcts.unspec.model;

import lombok.Data;

import java.util.List;

@Data
public class Claim {
    private List<Long> claimantIds;
    private List<Long> defendantIds;
}
