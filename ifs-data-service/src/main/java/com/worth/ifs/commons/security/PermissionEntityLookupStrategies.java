package com.worth.ifs.commons.security;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PermissionEntityLookupStrategies {
    String value() default "(no description)";
}
