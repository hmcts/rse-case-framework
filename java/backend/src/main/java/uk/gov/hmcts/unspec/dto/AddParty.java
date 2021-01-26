package uk.gov.hmcts.unspec.dto;

import lombok.Data;
import uk.gov.hmcts.ccf.XUI;

@Data
public class AddParty {

    @XUI(label = "Title")
    private String title;
    @XUI(label = "Forename")
    private String firstName;
    @XUI(label = "Surname")
    private String lastName;
    private String dateOfBirth;

}
