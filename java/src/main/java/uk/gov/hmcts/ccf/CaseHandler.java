package uk.gov.hmcts.ccf;

import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public interface CaseHandler {
    JsonNode get(Long caseId);

    String search(Map<String, String> params);
}
