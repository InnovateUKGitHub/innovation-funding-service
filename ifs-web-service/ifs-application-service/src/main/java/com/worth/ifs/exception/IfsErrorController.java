package com.worth.ifs.exception;

import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE;
import static org.springframework.http.MediaType.TEXT_HTML;
/**
 * For dealing with errors that do not even reach a controller. For errors that result in a controller throwing an {@link Exception} see {@link ErrorControllerAdvice}
 */
@Controller
public class IfsErrorController extends BasicErrorController {

    public IfsErrorController() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    @Override
    public ModelAndView errorHtml(HttpServletRequest request) {
        final Map<String, Object> errorAttributes = getErrorAttributes(request, false);
        final Object status = errorAttributes.get("status");
        if (status instanceof Integer){
            if (status.equals(NOT_FOUND.value())) {
                final Map<String, Object> model = getErrorAttributes(request, isIncludeStackTrace(request, TEXT_HTML));
                return new ModelAndView(NOT_FOUND.toString());
            } else if (status.equals(PAYLOAD_TOO_LARGE)){
                final Map<String, Object> model = getErrorAttributes(request, isIncludeStackTrace(request, TEXT_HTML));
                return new ModelAndView(PAYLOAD_TOO_LARGE.toString());
            }
        }
        return super.errorHtml(request);
    }
}
