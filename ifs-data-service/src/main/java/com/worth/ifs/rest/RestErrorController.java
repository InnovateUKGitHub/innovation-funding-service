package com.worth.ifs.rest;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorResponse;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.worth.ifs.commons.error.CommonErrors.forbiddenError;
import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_UNEXPECTED_ERROR;
import static org.springframework.http.HttpStatus.*;

/**
 * This Error Controller catches errors thrown by the DispatcherServlet and low-level Spring MVC and Security code.  This Controller
 * effectively converts errors that are encountered before Controller code is ever executed into RestErrorResponses.
 *
 * Examples of these types of low-level errors are requesting paths for which there are no Controllers to handle, and Spring Security
 * exceptions for attempting to access a forbidden path.
 */
@RestController
public class RestErrorController extends AbstractErrorController implements org.springframework.boot.autoconfigure.web.ErrorController {

    private static final String PATH = "/error";

    public RestErrorController() {
        super(new DefaultErrorAttributes());
    }

    public RestErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping(PATH)
    public ResponseEntity<RestErrorResponse> error(HttpServletRequest request) {
        Map<String, Object> errorAttributes = getErrorAttributes(request, false);
        Integer status = (Integer) errorAttributes.get("status");

        if (status != null) {
            if (NOT_FOUND.value() == status) {
                RestErrorResponse restErrorResponse = new RestErrorResponse(new Error(GENERAL_NOT_FOUND, NOT_FOUND));
                return new ResponseEntity<>(restErrorResponse, restErrorResponse.getStatusCode());
            } else if (FORBIDDEN.value() == status) {
                RestErrorResponse restErrorResponse = new RestErrorResponse(forbiddenError());
                return new ResponseEntity<>(restErrorResponse, restErrorResponse.getStatusCode());
            } else {
                RestErrorResponse restErrorResponse = new RestErrorResponse(new Error(GENERAL_UNEXPECTED_ERROR, HttpStatus.valueOf(status)));
                return new ResponseEntity<>(restErrorResponse, restErrorResponse.getStatusCode());
            }
        }
        RestErrorResponse fallbackResponse = new RestErrorResponse(internalServerErrorError());
        return new ResponseEntity<>(fallbackResponse, INTERNAL_SERVER_ERROR);
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}