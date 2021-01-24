package uk.gov.hmcts.ccf.definition;

import uk.gov.hmcts.ccf.ICase;

public interface ICaseView<T extends ICase> {
    String getTab();

    void render(ICaseRenderer renderer, T theCase);
}
