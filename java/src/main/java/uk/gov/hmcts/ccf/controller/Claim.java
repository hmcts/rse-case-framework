package uk.gov.hmcts.ccf.controller;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.generated.enums.ClaimState;

@Data
@NoArgsConstructor
public class Claim {
    Long claimId;
    Long caseId;
    Long lowerAmount;
    Long higherAmount;
    ClaimState state;
    ClaimParties parties;
}
