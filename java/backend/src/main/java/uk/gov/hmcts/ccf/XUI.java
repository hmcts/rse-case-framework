package uk.gov.hmcts.ccf;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface XUI {

    String label() default "";

    String hint() default "";

    String showCondition() default "";

    String typeParameter() default "";

    boolean showSummaryContent() default false;

    boolean ignore() default false;
}
