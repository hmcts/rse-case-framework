package uk.gov.hmcts.ccf;

import de.cronn.reflection.util.PropertyUtils;
import de.cronn.reflection.util.TypedPropertyGetter;
import net.jodah.typetools.TypeResolver;

import java.beans.PropertyDescriptor;
import java.util.Collection;

public abstract class XUISearchHandler<T extends XUISearchHandler.XUISearchResult> {

    public String fieldLabel(TypedPropertyGetter<T, ?> getter) {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(XUISearchHandler.class, this.getClass());
        Class<T> clazz = (Class<T>) typeArgs[0];

        XUI xui = PropertyUtils.getAnnotationOfProperty(clazz, getter, XUI.class);
        if (null != xui) {
            return xui.label();
        }
        return "";
    }

    public String fieldName(TypedPropertyGetter<T, ?> getter) {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(XUISearchHandler.class, this.getClass());
        Class<T> clazz = (Class<T>) typeArgs[0];

        return PropertyUtils.getPropertyName(clazz, getter);
    }

    public Class fieldType(TypedPropertyGetter<T, ?> getter) {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(XUISearchHandler.class, this.getClass());
        Class<T> clazz = (Class<T>) typeArgs[0];
        PropertyDescriptor descriptor = de.cronn.reflection.util.PropertyUtils
            .getPropertyDescriptor(clazz, getter);
        return descriptor.getPropertyType();
    }

    public abstract void configureColumns(ColumnMapper<T> mapper);

    public abstract Collection<? extends XUISearchResult> search(ESQueryParser.ESQuery query);

    public interface XUISearchResult {
        Long getCaseId();
    }
}
