package uk.gov.hmcts.ccf.types.fields;

import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.ccf.definition.CaseListField;

@Getter
@Setter
public class TwoFields {
    @CaseListField(label = "Foo Bar")
    private String fooBar = "foo";
    private String without = "bar";
}
