package org.innovateuk.ifs.commons.validation.constraints;

import org.innovateuk.ifs.commons.validation.FieldComparisonValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to compare two fields with a provided @{BiPredicate}
 */

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FieldComparisonValidator.class)
@Repeatable(FieldComparisonContainer.class)
public @interface FieldComparison {
    String message() default "{constraints.fieldcomparison}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String firstField();
    String secondField();
    Class<?> predicate();

    @Target({TYPE})
    @Retention(RUNTIME)
    @interface List {
        FieldComparison[] value();
    }
}
