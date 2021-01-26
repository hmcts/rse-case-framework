package uk.gov.hmcts.unspec.dto;

import lombok.Data;
import uk.gov.hmcts.ccf.XUI;

@Data
public class AddParty {
    @XUI(label = "Forename")
    private String forename;
    @XUI(label = "Surname")
    private String surname;
}
