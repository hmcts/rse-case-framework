package uk.gov.hmcts.ccf.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.unspec.dto.Party;

import java.util.List;

@Data
@AllArgsConstructor
public class CaseParty {
    Long partyId;
    Party data;
    PartyClaims claims;

    @NoArgsConstructor
    @Data
    static class PartyClaims {
        List<Long> claimant;
        List<Long> defendant;
    }
}
