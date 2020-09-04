package uk.gov.hmcts.ccf.demo.dto;

import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema;
import lombok.Data;

@Data
public class SolicitorReferences {
    @JsonSchema(title = "Claimant's legal representative's reference")
    private String claimantReference;
    @JsonSchema(title = "Defendant's legal representative's reference")
    private String defendantReference;
}
