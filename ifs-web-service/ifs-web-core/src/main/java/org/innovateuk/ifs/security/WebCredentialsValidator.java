package org.innovateuk.ifs.security;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.CredentialsValidator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;

@Component
public class WebCredentialsValidator implements CredentialsValidator {

    public static final Cache<String, UserResource> USER_CACHE
            = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();

    @Autowired
    private UserRestService userRestService;

    @Override
    public RestResult<UserResource> retrieveUserByUid(String token) {
        return retrieveUserByUid(token, false);
    }

    @Override
    public RestResult<UserResource> retrieveUserByUid(String uid, boolean expireCache) {

        if (expireCache) {
            USER_CACHE.invalidate(uid);
        }

        UserResource cachedUser = USER_CACHE.getIfPresent(uid);

        if (cachedUser != null) {
            return restSuccess(cachedUser);
        }

        RestResult<UserResource> user = userRestService.retrieveUserResourceByUid(uid);
        user.ifSuccessful(u -> USER_CACHE.put(uid, u));
        return user;
    }
}
