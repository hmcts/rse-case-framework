package uk.gov.hmcts.ccf;

import de.cronn.reflection.util.TypedPropertyGetter;

public interface ColumnMapper<T extends XUISearchHandler.XUISearchResult> {
    ColumnMapper<T> column(TypedPropertyGetter<T, ?> getter);
}
