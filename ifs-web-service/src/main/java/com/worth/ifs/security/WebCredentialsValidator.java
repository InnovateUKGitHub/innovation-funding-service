package com.worth.ifs.security;

import com.worth.ifs.commons.security.CredentialsValidator;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.service.UserRestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class WebCredentialsValidator implements CredentialsValidator {


    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    UserRestService userRestService;

    @Override
    public User retrieveUserByEmailAndPassword(String emailAddress, String password) {
        User user = userRestService.retrieveUserByEmailAndPassword(emailAddress, password);
        return user;
    }

    @Override
    public User retrieveUserByToken(String token) {
        try {
            return userRestService.retrieveUserByToken(token);
        } catch (HttpClientErrorException e) {
            log.error(e);
            return null;
        }
    }
}
