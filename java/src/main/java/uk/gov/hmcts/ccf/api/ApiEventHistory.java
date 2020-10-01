package uk.gov.hmcts.ccf.api;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiEventHistory {

    private int sequenceNumber;
    private String id;
    private String state;
    private LocalDateTime timestamp;
    private String userForename;
    private String userSurname;

}
