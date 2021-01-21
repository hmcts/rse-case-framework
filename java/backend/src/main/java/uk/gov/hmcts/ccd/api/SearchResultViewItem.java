package uk.gov.hmcts.ccd.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Map;

@Builder
public class SearchResultViewItem {
    @JsonProperty("case_id")
    private String caseId;
    @JsonProperty("case_fields")
    private Map<String, Object> fields;
    @JsonProperty("case_fields_formatted")
    private Map<String, Object> fieldsFormatted;

    public SearchResultViewItem() {
        // Default constructor for JSON mapper
    }

    public SearchResultViewItem(final String caseId,
                                final Map<String, Object> fields,
                                final Map<String, Object> fieldsFormatted) {
        this.caseId = caseId;
        this.fields = fields;
        this.fieldsFormatted = fieldsFormatted;
    }

    public String getCaseId() {
        return caseId;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public Map<String, Object> getFieldsFormatted() {
        return fieldsFormatted;
    }
}
