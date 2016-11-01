package com.worth.ifs.commons.security;


import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Transactional(readOnly = true)
public @interface PermissionRules {
}
