package org.innovateuk.ifs.shibboleth.api.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

public interface LdapExceptionHandlers {

    @ExceptionHandler(EmptyResultDataAccessException.class)
    default ResponseEntity<Void> handleNonExistentIdentityException(final HttpServletRequest request,
        final EmptyResultDataAccessException exception) {

        final Logger logger = LoggerFactory.getLogger(LdapExceptionHandlers.class);
        logger.debug("Unable to find any entry in LDAP [" + request.getRequestURL() + "]", exception);

        return ResponseEntity.notFound().build();
    }

}
