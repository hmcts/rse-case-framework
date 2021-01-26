package uk.gov.hmcts.ccf;

import de.cronn.reflection.util.PropertyUtils;
import de.cronn.reflection.util.TypedPropertyGetter;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseUpdateViewEvent;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldTypeDefinition;
import uk.gov.hmcts.ccd.domain.model.definition.FixedListItemDefinition;

import java.beans.PropertyDescriptor;
import java.util.UUID;

public class EventBuilder<T> {

    private Class<T> clazz;
    private CaseUpdateViewEvent.CaseUpdateViewEventBuilder builder = CaseUpdateViewEvent.builder();

    public CaseUpdateViewEvent build() {
        return builder.build();
    }

    public EventBuilder(Class<T> clazz) {
        this.clazz = clazz;
    }

    public EventBuilder<T> field(TypedPropertyGetter<T, ?> getter) {
        String id = PropertyUtils.getPropertyName(clazz, getter);
        PropertyDescriptor descriptor = de.cronn.reflection.util.PropertyUtils
            .getPropertyDescriptor(clazz, getter);
        Class propertyType = descriptor.getPropertyType();

        if (propertyType.isEnum()) {
            builder.caseField(CaseViewField.builder()
                .id(id)
                .fieldTypeDefinition(buildFixedList(propertyType))
                .build());
        }
        return this;
    }

    private FieldTypeDefinition buildFixedList(Class propertyType) {
        FieldTypeDefinition.FieldTypeDefinitionBuilder fb = FieldTypeDefinition.builder();
        fb.id(UUID.randomUUID().toString())
            .type("FixedRadioList");

        int t = 1;
        for (Object e : propertyType.getEnumConstants()) {
            FixedListItemDefinition.FixedListItemDefinitionBuilder ib = FixedListItemDefinition.builder();
            ib.code(e.toString());
            ib.order(String.valueOf(t++));
            if (e instanceof HasLabel) {
                ib.label(((HasLabel) e).getLabel());
            }
            fb.fixedListItemDefinition(ib.build());
        }

        return fb.build();
    }
}
