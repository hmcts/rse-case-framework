package uk.gov.hmcts.ccf;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import de.cronn.reflection.util.PropertyUtils;
import de.cronn.reflection.util.TypedPropertyGetter;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseUpdateViewEvent;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldTypeDefinition;
import uk.gov.hmcts.ccd.domain.model.definition.FixedListItemDefinition;
import uk.gov.hmcts.ccd.domain.model.definition.WizardPage;
import uk.gov.hmcts.ccd.domain.model.definition.WizardPageField;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

public class EventBuilder<T> {

    private Class<T> clazz;
    private CaseUpdateViewEvent.CaseUpdateViewEventBuilder builder = CaseUpdateViewEvent.builder();
    private Multimap<Integer, String> fieldPageMap = ArrayListMultimap.create();
    private int currentPage = 1;
    
    public CaseUpdateViewEvent build() {
        for (int t = 1; t <= currentPage; t++) {
            Collection<String> fieldNames =
                fieldPageMap.get(t);

            Collection<WizardPageField> fields = Lists.newArrayList();
            int i = 1;
            for (String fieldName : fieldNames) {
                fields.add(WizardPageField.builder()
                    .order(i++)
                    .caseFieldId(fieldName)
                    .pageColumnNumber(1)
                    .build());
            }

            builder.wizardPage(WizardPage.builder()
                .order(t)
                .wizardPageFields(fields)
                .build());
        }
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
        } else if (propertyType.equals(LocalDate.class)) {
            builder.caseField(CaseViewField.builder()
                .id(id)
                .fieldTypeDefinition(FieldTypeDefinition.builder()
                    .id("Date")
                    .type("Date")
                    .build())
                .build());
        } else if (propertyType.equals(String.class)) {
            builder.caseField(CaseViewField.builder()
                .id(id)
                .fieldTypeDefinition(FieldTypeDefinition.builder()
                    .id("TextArea")
                    .type("TextArea")
                    .build())
                .build());
        } else if (propertyType.equals(boolean.class)) {
            builder.caseField(CaseViewField.builder()
                .id(id)
                .fieldTypeDefinition(FieldTypeDefinition.builder()
                    .id("YesOrNo")
                    .type("YesOrNo")
                    .build())
                .build());
        } else {
            throw new RuntimeException("Unimplemented type:" + propertyType);
        }

        CaseViewField f = builder.getCaseFields().get(builder.getCaseFields().size() - 1);
        fieldPageMap.put(currentPage, f.getId());

        XUI xui = PropertyUtils.getAnnotationOfProperty(this.clazz, getter, XUI.class);
        if (xui != null) {
            f.setLabel(xui.label());
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
