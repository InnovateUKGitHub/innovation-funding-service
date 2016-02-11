package com.worth.ifs.commons.service;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface RestCacheInvalidateResult {

}
