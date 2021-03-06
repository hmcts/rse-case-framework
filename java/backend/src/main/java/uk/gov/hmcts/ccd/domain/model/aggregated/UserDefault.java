package uk.gov.hmcts.ccd.domain.model.aggregated;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.ccd.domain.model.definition.JurisdictionDefinition;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class UserDefault {
    private String id;
    private List<JurisdictionDefinition> jurisdictionDefinitions;


    @JsonProperty("work_basket_default_jurisdiction")
    private String workBasketDefaultJurisdiction;


    @JsonProperty("work_basket_default_case_type")
    private String workBasketDefaultCaseType;


    @JsonProperty("work_basket_default_state")

    private String workBasketDefaultState;

    public void addJurisdiction(JurisdictionDefinition jurisdictionDefinition) {
        if (jurisdictionDefinitions == null) {
            jurisdictionDefinitions = new ArrayList<>();
        }
        jurisdictionDefinitions.add(jurisdictionDefinition);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<JurisdictionDefinition> getJurisdictionDefinitions() {
        return jurisdictionDefinitions;
    }

    public void setJurisdictionDefinitions(List<JurisdictionDefinition> jurisdictionDefinitions) {
        this.jurisdictionDefinitions = jurisdictionDefinitions;
    }

    public String getWorkBasketDefaultJurisdiction() {
        return workBasketDefaultJurisdiction;
    }

    public void setWorkBasketDefaultJurisdiction(final String workBasketDefaultJurisdiction) {
        this.workBasketDefaultJurisdiction = workBasketDefaultJurisdiction;
    }

    public String getWorkBasketDefaultCaseType() {
        return workBasketDefaultCaseType;
    }

    public void setWorkBasketDefaultCaseType(final String workBasketDefaultCaseType) {
        this.workBasketDefaultCaseType = workBasketDefaultCaseType;
    }

    public String getWorkBasketDefaultState() {
        return workBasketDefaultState;
    }

    public void setWorkBasketDefaultState(final String workBasketDefaultState) {
        this.workBasketDefaultState = workBasketDefaultState;
    }

    public List<String> getJurisdictionsId() {
        return this.jurisdictionDefinitions.stream().map(JurisdictionDefinition::getId).collect(toList());
    }
}
