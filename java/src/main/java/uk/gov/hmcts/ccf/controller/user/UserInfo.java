package uk.gov.hmcts.ccf.controller.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String username;
    private String forename;
    private String surname;
    private Set<String> roles;
}
