package uk.gov.hmcts.ccf;

public interface ICase {
    String getCaseId();
    <T extends Enum<T>> T getState();
}
