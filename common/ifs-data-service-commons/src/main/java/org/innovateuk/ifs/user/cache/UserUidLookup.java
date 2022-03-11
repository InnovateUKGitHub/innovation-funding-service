package org.innovateuk.ifs.user.cache;

import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.innovateuk.ifs.IfsCacheConstants.RESULT_FAILURE;
import static org.innovateuk.ifs.user.cache.DataServiceCacheConstants.CACHE_NAME_USERID;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Cacheable(cacheNames=CACHE_NAME_USERID, unless = RESULT_FAILURE)
public @interface UserUidLookup {
}
