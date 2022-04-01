package org.innovateuk.ifs.user.cache;

import org.springframework.cache.annotation.CachePut;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.innovateuk.ifs.IfsCacheConstants.RESULT_FAILURE;
import static org.innovateuk.ifs.user.cache.DataServiceCacheConstants.CACHE_NAME_USERID;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@CachePut(value = CACHE_NAME_USERID,
        key = "#result.getOptionalSuccessObject().orElse(new org.innovateuk.ifs.user.resource.UserResource('')).uid",
        unless = RESULT_FAILURE)
public @interface UserUpdate {
}
