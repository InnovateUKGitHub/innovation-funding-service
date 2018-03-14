package org.innovateuk.ifs.commons.validation.constraints;

import org.innovateuk.ifs.commons.validation.FieldLargerValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FieldLargerValidator.class)
public @interface FieldLarger {
    String message() default "{constraints.fieldlargerthan}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String firstField();
    String secondField();

    @Target({TYPE})
    @Retention(RUNTIME)
    @interface List {
        FieldLarger[] value();
    }
}
