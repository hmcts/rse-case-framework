package uk.gov.hmcts.ccf.types.model;

import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccf.ICase;
import uk.gov.hmcts.ccf.definition.CaseListField;
import uk.gov.hmcts.ccf.definition.CaseSearchableField;
import uk.gov.hmcts.ccf.definition.ComplexType;

@NoArgsConstructor
public class FakeCase implements ICase {

    // Annotated fields are editable in the CCD UI.
    @CaseListField(label = "Defendant Name")
    @CaseSearchableField(label = "Defendant name", order = 2)
    private String defendantName = "Peter";

    @CaseSearchableField(label = "Prosecutor name", order = 1)
    @CaseListField(label = "Prosecutor Name")
    private String prosecutorName = "Paul";

    @ComplexType
    private Party party = new Party();

    public static final FakeCase C = new FakeCase("D", "P");

    public FakeCase(String defendantName, String prosecutorName) {
        this.defendantName = defendantName;
        this.prosecutorName = prosecutorName;
    }

    @Override
    public String getCaseId() {
        return "fake id";
    }

    @Override
    public FakeState getState() {
        return FakeState.Closed;
    }

    public Party getParty() {
        return party;
    }
}
