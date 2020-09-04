package uk.gov.hmcts.ccf.demo.event;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.github.imifou.jsonschema.module.addon.annotation.JSData;
import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonClassDescription("Close the case")
public class CloseCase {
    @JsonSchema(title = "Reason for closure")
    private String reason;
}
