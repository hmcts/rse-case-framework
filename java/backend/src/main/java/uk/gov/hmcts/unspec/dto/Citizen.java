package uk.gov.hmcts.unspec.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Citizen {
    private int count;
    private String title;
    private String forename;
    private String surname;
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;
    private String status;
}
