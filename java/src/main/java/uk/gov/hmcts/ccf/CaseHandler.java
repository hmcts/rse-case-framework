package uk.gov.hmcts.ccf;

import com.fasterxml.jackson.databind.JsonNode;

public interface CaseHandler {
    JsonNode get(Long caseId);

}
