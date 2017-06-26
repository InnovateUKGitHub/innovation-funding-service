package org.innovateuk.ifs.exception;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This controller can handle all Exceptions, so the user should always gets a
 * nice looking error page, or a json error message is returned.
 * NOTE: Make sure every (non json) response uses createExceptionModelAndView as it also sets login and dashboard links
 */
@ControllerAdvice
public class ErrorControllerAdvice extends CommonErrorControllerAdvice {
    private static final Log LOG = LogFactory.getLog(ErrorControllerAdvice.class);

    public ErrorControllerAdvice() {
        super();
    }

    public ErrorControllerAdvice(Environment env, MessageSource messageSource) {
        super();
        this.env = env;
        this.messageSource = messageSource;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)     // 400
    @ExceptionHandler(value = {AutoSaveElementException.class})
    public @ResponseBody ObjectNode jsonAutosaveResponseHandler(AutoSaveElementException e) throws AutoSaveElementException {
        LOG.debug("ErrorController jsonAutosaveResponseHandler", e);
        return e.createJsonResponse();
    }

}
