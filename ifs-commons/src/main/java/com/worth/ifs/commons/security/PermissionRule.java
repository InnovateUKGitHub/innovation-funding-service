package com.worth.ifs.commons.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionRule {

    /**
     * @return The action key that is being secured (e.g. 'READ', 'UPDATE', 'UPLOAD_DOCUMENT')
     */
    String value();

    /**
     * @return A description of the business logic being enforced by this Permission Rule
     */
    String description() default "";

    /**
     * @return Additional comments / concerns about this Permission Rule
     */
    String additionalComments() default "";

    /**
     * @return A description of a particular state of affairs that makes this rule alter its behaviour if key entities
     * involved in the business logic are in particular states (e.g. this Rule being enforeced only if a Competition is
     * in the Funders' Panel state)
     */
    String particularBusinessState() default "";
}
