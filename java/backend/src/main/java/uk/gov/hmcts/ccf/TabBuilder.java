package uk.gov.hmcts.ccf;

import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewTab;
import uk.gov.hmcts.ccd.domain.model.definition.FieldTypeDefinition;

import java.rmi.server.UID;

public class TabBuilder {

    private final CaseViewTab tab;
    private final CaseViewBuilder parent;

    public TabBuilder(CaseViewBuilder parent, CaseViewTab tab) {
        this.parent = parent;
        this.tab = tab;
    }

    public TabBuilder field(String label, String value, String hint) {
        CaseViewField f = CaseViewField.builder()
            .id(new UID().toString())
            .value(value)
            .hintText(hint)
            .label(label)
            .fieldTypeDefinition(type("Text")).build();
        tab.getFields().add(f);
        return this;
    }

    private FieldTypeDefinition type(String type) {
        return FieldTypeDefinition.builder()
            .id(type)
            .type(type)
            .build();
    }

    public CaseViewBuilder build() {
        return this.parent;
    }
}
