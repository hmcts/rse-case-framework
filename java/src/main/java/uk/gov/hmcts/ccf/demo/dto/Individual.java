package uk.gov.hmcts.ccf.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Individual extends Party {
    private String title;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
}
