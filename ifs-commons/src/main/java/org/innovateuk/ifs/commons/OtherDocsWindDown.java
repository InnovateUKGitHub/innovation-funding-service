package org.innovateuk.ifs.commons;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Annotation to mark old "other documents" code that is only necessary while we support this in project setup, without
 * the Sonarqube penalties associated with TODOs or deprecation. Anything with this annotation will become dead code,
 * and can be safely removed once the relevant migration script has been run.
 */
@Retention(SOURCE)
@Target(value = {FIELD, METHOD, LOCAL_VARIABLE, PACKAGE, TYPE, CONSTRUCTOR, TYPE_PARAMETER, TYPE_USE})
public @interface OtherDocsWindDown {
    /**
     * Using this annotation implies that the code relates to other documents functionality - any other comments
     * associated with the particular code being annotated can be added here.
     */
    String additionalComments() default "";
}