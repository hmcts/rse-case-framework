package uk.gov.hmcts.ccf.demo.api;

import com.github.imifou.jsonschema.module.addon.TypeFormat;
import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiEventHistory {

    private int sequenceNumber;
    private String id;
    private String state;
    @JsonSchema(format= TypeFormat.DATE_TIME)
    private LocalDateTime timestamp;
    @JsonSchema(maxLength = 100)
    private String userForename;
    private String userSurname;

}
