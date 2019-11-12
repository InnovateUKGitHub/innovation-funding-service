package org.innovateuk.ifs.user.cache;

import org.springframework.cache.annotation.CachePut;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@CachePut(value = "userUid", key = "#result.getSuccess().uid", unless = "#result.isFailure()")
public @interface UserUpdate {
}
