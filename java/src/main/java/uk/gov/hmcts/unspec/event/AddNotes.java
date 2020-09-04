package uk.gov.hmcts.unspec.event;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema;
import lombok.Data;

@Data
@JsonClassDescription("Add case notes")
public class AddNotes {
    @JsonSchema(title = "Case notes")
    private String notes;
}
