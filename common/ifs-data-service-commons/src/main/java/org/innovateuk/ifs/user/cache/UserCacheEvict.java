package org.innovateuk.ifs.user.cache;

import org.springframework.cache.annotation.CacheEvict;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.innovateuk.ifs.user.cache.DataServiceCacheConstants.CACHE_NAME_USERID;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@CacheEvict(CACHE_NAME_USERID)
public @interface UserCacheEvict {
}
