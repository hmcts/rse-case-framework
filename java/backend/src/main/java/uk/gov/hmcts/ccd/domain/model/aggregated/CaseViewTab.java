package uk.gov.hmcts.ccd.domain.model.aggregated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.ToString;

import java.util.List;

@ToString
public class CaseViewTab {
    private String id;
    private String label;
    private Integer order;
    private List<CaseViewField> fields = Lists.newArrayList();
    @JsonProperty("show_condition")
    private String showCondition;
    private String role;

    public CaseViewTab() {
        // default constructor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<CaseViewField> getFields() {
        return fields;
    }

    public void setFields(List<CaseViewField> fields) {
        this.fields = fields;
    }

    public String getShowCondition() {
        return showCondition;
    }

    public void setShowCondition(String showCondition) {
        this.showCondition = showCondition;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }
}
