package uk.gov.hmcts.unspec.dto;

import lombok.Data;

import java.util.Date;

@Data
public class Citizen {
    private int count;
    private String title;
    private String forename;
    private String surname;
    private Date date_of_birth;
}
