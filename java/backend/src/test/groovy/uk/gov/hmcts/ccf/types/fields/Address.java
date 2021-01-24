package uk.gov.hmcts.ccf.types.fields;

import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.ccf.definition.CaseListField;
import uk.gov.hmcts.ccf.definition.FieldLabel;

@Getter
@Setter
public class Address {
    @CaseListField(label = "Nested field")
    @FieldLabel(value = "Line 1")
    private String line1 = "test line 1";

    @FieldLabel(value = "Line 2")
    private String line2 = "line 2";
}
