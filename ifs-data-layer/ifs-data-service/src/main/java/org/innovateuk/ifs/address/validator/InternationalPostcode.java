package org.innovateuk.ifs.address.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InternationalPostcodeValidator.class)
public @interface InternationalPostcode {

    String message() default "The postcode should not exceed 255 characters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * The argument field which will be tested.
     *
     * @return
     */
    String argument();


}
