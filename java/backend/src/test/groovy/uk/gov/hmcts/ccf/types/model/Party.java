package uk.gov.hmcts.ccf.types.model;

import uk.gov.hmcts.ccf.definition.ComplexType;
import uk.gov.hmcts.ccf.definition.FieldLabel;
import uk.gov.hmcts.ccf.types.fields.Address;

public class Party {
    @FieldLabel(value = "Party Name")
    String name = "test";
    @FieldLabel(value = "Party Address")
    @ComplexType
    Address address = new Address();
}
