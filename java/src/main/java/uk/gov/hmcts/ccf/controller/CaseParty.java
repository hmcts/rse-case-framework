package uk.gov.hmcts.ccf.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.hmcts.unspec.dto.Party;

@Data
@AllArgsConstructor
public class CaseParty {
    Long partyId;
    Party data;
    Claims claims;
}
