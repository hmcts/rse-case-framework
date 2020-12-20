package uk.gov.hmcts.ccf.controller.claim;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.unspec.dto.Party;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimParties {
    List<Party> claimants;
    List<Party> defendants;
}
