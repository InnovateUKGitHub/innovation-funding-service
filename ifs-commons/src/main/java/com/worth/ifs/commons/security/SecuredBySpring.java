package com.worth.ifs.commons.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An Annotation that can be used to document secured methods that cannot be secured through the @PermissionRule framework.
 *
 * Examples of these would be global actions that are not related to individual entities e.g. being able to use the
 * Companies House lookup.  Whereas we could've secured the results of the Companies House call, we want to prevent
 * instead the usage of the API call altogether to prevent costs being incurred.  We have no entity to secure against in
 * this case, so we lock it down with a standard Spring Security global role check instead.
 *
 * This annotation gives us a way to add a description to standard Spring Security checks in a way that can be included
 * in the same documentation that covers the @PermissionRule mechanism
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SecuredBySpring {

    /**
     * The type of Action that this secured method is securing against e.g. "READ", "UPDATE"
     * @return
     */
    String value();

    /**
     * A description of the Security rule in place
     *
     * @return
     */
    String description();

    /**
     * A field for capturing any additional comments or concerns around the secured method
     *
     * @return
     */
    String additionalComments() default "";

    /**
     * @return A description of a particular state of affairs that makes this rule alter its behaviour if key entities
     * involved in the business logic are in particular states (e.g. this Rule being enforeced only if a Competition is
     * in the Funders' Panel state)
     */
    String particularBusinessState() default "";

    /**
     * A field for capturing a secured entity type if available
     *
     * @return
     */
    Class<?> securedType() default Void.class;
}