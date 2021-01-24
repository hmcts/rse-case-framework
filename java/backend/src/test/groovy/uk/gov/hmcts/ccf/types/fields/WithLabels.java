package uk.gov.hmcts.ccf.types.fields;


import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.ccf.definition.FieldLabel;

@Getter
@Setter
@FieldLabel(value = "parent")
public class WithLabels {

    @FieldLabel(value = "child")
    private String mystring = "value";
}
