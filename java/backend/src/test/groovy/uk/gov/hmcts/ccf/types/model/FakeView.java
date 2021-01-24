package uk.gov.hmcts.ccf.types.model;

import uk.gov.hmcts.ccf.definition.BaseCaseView;

public class FakeView extends BaseCaseView<FakeCase> {
    public String getTab() {
        return "tab2";
    }

    @Override
    protected void onRender(FakeCase theCase) {
        render(null);
        render(theCase.getCaseId());
        render(theCase.getParty());
    }
}
