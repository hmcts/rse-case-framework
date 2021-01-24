package uk.gov.hmcts.ccf.definition;

import java.util.function.Supplier;

public interface FieldRenderer {
    void render(Supplier<Object> getter);
}
