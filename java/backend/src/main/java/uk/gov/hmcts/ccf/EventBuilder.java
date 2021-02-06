package uk.gov.hmcts.ccf;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import de.cronn.reflection.util.PropertyUtils;
import de.cronn.reflection.util.TypedPropertyGetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseUpdateViewEvent;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldTypeDefinition;
import uk.gov.hmcts.ccd.domain.model.definition.FixedListItemDefinition;
import uk.gov.hmcts.ccd.domain.model.definition.WizardPage;
import uk.gov.hmcts.ccd.domain.model.definition.WizardPageField;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public class EventBuilder<T> {

    private Class<T> clazz;
    private CaseUpdateViewEvent.CaseUpdateViewEventBuilder builder = CaseUpdateViewEvent.builder();
    private Multimap<Integer, FieldInfo> fieldPageMap = ArrayListMultimap.create();
    private Map<Integer, String> pageLabels = Maps.newHashMap();
    private int currentPage = 1;
    private BiConsumer<Long, T> handler;
    private String showGroup;

    public CaseUpdateViewEvent build() {
        for (int t = 1; t <= currentPage; t++) {
            Collection<FieldInfo> fieldNames =
                fieldPageMap.get(t);

            Collection<WizardPageField> fields = Lists.newArrayList();
            int i = 1;
            for (FieldInfo info : fieldNames) {
                fields.add(WizardPageField.builder()
                    .order(i++)
                    .caseFieldId(info.getId())
                    .pageColumnNumber(info.getColumn())
                    .build());
            }

            builder.wizardPage(WizardPage.builder()
                .order(t)
                .id(String.valueOf(t))
                .label(pageLabels.containsKey(t) ? pageLabels.get(t) : "Page " + t)
                .wizardPageFields(fields)
                .build());
        }
        return builder.build();
    }

    public EventBuilder(Class<T> clazz, String id, String label) {
        this.clazz = clazz;
        this.builder.id(id);
        this.builder.name(id);
        this.builder.description(id);
        this.pageLabels.put(currentPage, label);
    }

    public EventBuilder(Class<T> clazz) {
        this.clazz = clazz;
        this.builder.id("");
    }

    public EventBuilder<T> withHandler(BiConsumer<Long, T> handler) {
        this.handler = handler;
        return this;
    }

    public <U> EventBuilder<T> multiSelect(TypedPropertyGetter<T, ? extends Set<U>> getter,
                                           Map<U, String> options) {
        return multiSelect(getter, options, 1);

    }

    public <U> EventBuilder<T> multiSelect(TypedPropertyGetter<T, ? extends Set<U>> getter,
                                           Map<U, String> options, int column) {
        String id = PropertyUtils.getPropertyName(clazz, getter);


        FieldTypeDefinition.FieldTypeDefinitionBuilder fb = FieldTypeDefinition.builder();
        fb.id(UUID.randomUUID().toString())
            .type("MultiSelectList");

        for (Map.Entry<U, String> entry : options.entrySet()) {
            FixedListItemDefinition.FixedListItemDefinitionBuilder ib = FixedListItemDefinition.builder();
            ib.code(entry.getKey().toString());
            ib.label(entry.getValue());
            fb.fixedListItemDefinition(ib.build());
        }

        XUI xui = PropertyUtils.getAnnotationOfProperty(this.clazz, getter, XUI.class);
        String label = "";
        if (xui != null) {
            label = xui.label();
        }

        fieldPageMap.put(currentPage, new FieldInfo(id, column, showGroup));

        builder.caseField(CaseViewField.builder()
            .id(id)
            .showCondition(showGroup)
            .showSummaryChangeOption(true)
            .label(label)
            .value(Lists.newArrayList())
            .fieldTypeDefinition(fb.build())
            .build());
        return this;
    }

    public EventBuilder<T> field(TypedPropertyGetter<T, ?> getter) {
        String id = PropertyUtils.getPropertyName(clazz, getter);
        PropertyDescriptor descriptor = de.cronn.reflection.util.PropertyUtils
            .getPropertyDescriptor(clazz, getter);
        Class propertyType = descriptor.getPropertyType();

        if (propertyType.isEnum()) {
            builder.caseField(CaseViewField.builder()
                .id(id)
                .showCondition(showGroup)
                .fieldTypeDefinition(buildFixedList(propertyType))
                .build());
        } else if (propertyType.equals(LocalDate.class)) {
            builder.caseField(CaseViewField.builder()
                .id(id)
                .showCondition(showGroup)
                .fieldTypeDefinition(FieldTypeDefinition.builder()
                    .id("Date")
                    .type("Date")
                    .build())
                .build());
        } else if (propertyType.equals(String.class)) {
            builder.caseField(CaseViewField.builder()
                .id(id)
                .showCondition(showGroup)
                .fieldTypeDefinition(FieldTypeDefinition.builder()
                    .id("Text")
                    .type("Text")
                    .build())
                .build());
        } else if (propertyType.equals(long.class)) {
            builder.caseField(CaseViewField.builder()
                .id(id)
                .showCondition(showGroup)
                .fieldTypeDefinition(FieldTypeDefinition.builder()
                    .id("Number")
                    .type("Number")
                    .build())
                .build());
        } else if (propertyType.equals(boolean.class)) {
            builder.caseField(CaseViewField.builder()
                .id(id)
                .showCondition(showGroup)
                .fieldTypeDefinition(FieldTypeDefinition.builder()
                    .id("YesOrNo")
                    .type("YesOrNo")
                    .build())
                .build());
        } else {
            throw new RuntimeException("Unimplemented type:" + propertyType);
        }

        CaseViewField f = builder.getCaseFields().get(builder.getCaseFields().size() - 1);
        this.builder.showSummary(true);
        f.setShowSummaryChangeOption(true);
        fieldPageMap.put(currentPage, new FieldInfo(f.getId(), 1, showGroup));

        XUI xui = PropertyUtils.getAnnotationOfProperty(this.clazz, getter, XUI.class);
        if (xui != null) {
            f.setLabel(xui.label());
            f.getFieldTypeDefinition().setMin(BigDecimal.valueOf(xui.min()));
            f.getFieldTypeDefinition().setMax(BigDecimal.valueOf(xui.max()));
            if (xui.type() != XUIType.Default) {
                f.getFieldTypeDefinition().setType(xui.type().toString());
            }
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

    public EventBuilder<T> nextPage() {
        currentPage++;
        return this;
    }

    public EventBuilder<T> showGroup(String showCondition) {
        this.showGroup = showCondition;
        return this;
    }

    @Data
    @AllArgsConstructor
    private class FieldInfo {
        private String id;
        private int column = 1;
        private String showCondition;
    }
}