package uk.gov.hmcts.ccf.definition;

public interface ICaseRenderer {
    void render(Object o);

    void render(Object o, String label);
}
