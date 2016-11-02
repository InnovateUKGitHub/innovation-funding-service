package com.worth.ifs.commons.security;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
// TODO INFUND-5955
@Transactional(readOnly = true)
public @interface PermissionEntityLookupStrategy {
}
