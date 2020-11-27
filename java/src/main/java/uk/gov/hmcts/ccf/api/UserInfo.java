package uk.gov.hmcts.ccf.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String username;
    private Set<String> roles;
}
