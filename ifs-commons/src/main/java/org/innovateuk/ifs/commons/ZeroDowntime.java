package org.innovateuk.ifs.commons;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Annotation to allow us to better keep track of zero downtime deploy tasks without the need for TODOs or deprecation
 * (which SonarQube penalizes the codebase for).  This will also allow us to track any tasks that have not yet been
 * picked up in subsequent sprints.
 */
@Retention(SOURCE)
@Target(value = {FIELD, METHOD, LOCAL_VARIABLE, PACKAGE, TYPE, CONSTRUCTOR, TYPE_PARAMETER, TYPE_USE})
public @interface ZeroDowntime {

    String reference();

    String description();
}
