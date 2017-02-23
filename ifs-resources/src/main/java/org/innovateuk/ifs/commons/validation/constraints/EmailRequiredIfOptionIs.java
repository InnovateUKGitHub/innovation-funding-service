package org.innovateuk.ifs.commons.validation.constraints;

import org.innovateuk.ifs.commons.validation.EmailRequiredIfOptionIsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Validation annotation to assert that a required email is not {@code null} or invalid if a separate predicate is met.
 */
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailRequiredIfOptionIsValidator.class)
@Repeatable(EmailRequiredIfOptionalIsContainer.class)
public @interface EmailRequiredIfOptionIs {
    String message() default "{constraints.emailRequiredIfOptionIs}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The field that must have a non blank value present to satisfy this validation constraint if the condition is met
     *
     * @return
     */
    String required();

    /**
     * The argument field which will be tested.
     *
     * @return
     */
    String argument();

    /**
     * The predicate of the condition. The argument field value will be matched against this. If they match then the required field must have a non blank value to satisfy this validation constraint.
     *
     * @return
     */
    long predicate();

    /**
     * Additional regular expression the email must comply with
     *
     * @return
     */
    String regexp();

    /**
     * Message to display when email is invalid
     *
     * @return
     */
    String invalidMessage();
}
