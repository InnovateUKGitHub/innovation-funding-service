package org.innovateuk.ifs.user.cache;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Caching(put = {
        @CachePut(value = "userUid", key = "#result.getSuccess().uid", unless = "#result.isFailure()")
})
public @interface UserUpdate {
}
