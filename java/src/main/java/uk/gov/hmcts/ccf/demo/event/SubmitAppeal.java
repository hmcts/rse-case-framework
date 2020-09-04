package uk.gov.hmcts.ccf.demo.event;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonClassDescription("Open an appeal")
public class SubmitAppeal {
    @JsonSchema(title = "Reason for appealing")
    private String reason;
}
