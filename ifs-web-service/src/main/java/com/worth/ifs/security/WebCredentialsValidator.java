package com.worth.ifs.security;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.CredentialsValidator;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.service.UserRestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;

@Component
public class WebCredentialsValidator implements CredentialsValidator {


    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    UserRestService userRestService;

    // TODO DW - INFUND-1555 - user rest service should be returning rest results
    @Override
    public RestResult<User> retrieveUserByEmailAndPassword(String emailAddress, String password) {
        return restSuccess(userRestService.retrieveUserByEmailAndPassword(emailAddress, password));
    }

    @Override
    public RestResult<User> retrieveUserByToken(String token) {
        return restSuccess(userRestService.retrieveUserByToken(token));
    }
}
