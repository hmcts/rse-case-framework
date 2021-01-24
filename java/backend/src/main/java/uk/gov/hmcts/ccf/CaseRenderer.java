package uk.gov.hmcts.ccf;

import com.google.common.collect.Lists;
import uk.gov.hmcts.ccd.domain.model.definition.CaseFieldDefinition;
import uk.gov.hmcts.ccf.definition.ICaseRenderer;

import java.util.List;

public class CaseRenderer implements ICaseRenderer {
    private List<CaseFieldDefinition> fields = Lists.newArrayList();

    @Override
    public void render(Object o) {
        render(o, "");
    }

    @Override
    public void render(Object o, String label) {
        if (null == o) {
            return;
        }
        CaseFieldDefinition field = ReflectionUtils.convert(o);
        if (null != field) {
            if (label != "") {
                field.setLabel(label);
            }
            fields.add(field);
            field.setOrder(0);
        }
    }

    public CaseFieldDefinition[] getFields() {
        return fields.toArray(new CaseFieldDefinition[0]);
    }
}
