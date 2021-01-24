package uk.gov.hmcts.ccf.types.nested;

import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.ccf.definition.CaseListField;
import uk.gov.hmcts.ccf.definition.ComplexType;
import uk.gov.hmcts.ccf.types.fields.Address;

@Getter
@Setter
public class WithSubClass {
    @CaseListField(label = "Foo Bar")
    public String fooBar = "foo";
    @ComplexType
    public Address subClass = new Address();
}
