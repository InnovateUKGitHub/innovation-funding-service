package org.innovateuk.ifs.shibboleth.api.endpoints;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.shibboleth.api.models.ExceptionResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Order(Ordered.LOWEST_PRECEDENCE)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleExceptions(final HttpServletRequest req, final Exception ex) {

        log.error("Generic problem handling on [{}] for [{}]", req.getRequestURL(), ex.getMessage());
        log.debug("Generic problem handling - stacktrace.", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionResponse(ex));
    }

}
