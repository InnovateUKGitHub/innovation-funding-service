package com.worth.ifs.controller;

import org.springframework.core.MethodParameter;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

/**
 * An extension of ServletModelAttributeMethodProcessor that prevents Spring MVC from throwing a BindingException if there
 * is a ControllerValidationHelper as a method parameter after an invalid @ModelAttribute argument, in the same way that
 * it would normally do if there was a BindingResult following an invalid @ModelAttribute argument
 */
public class ControllerValidationHandlerServletModelAttributeMethodProcessor extends ServletModelAttributeMethodProcessor {

    public ControllerValidationHandlerServletModelAttributeMethodProcessor(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    @Override
    protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter methodParam) {

        int i = methodParam.getParameterIndex();
        Class<?>[] paramTypes = methodParam.getMethod().getParameterTypes();

        //
        // Overridden this method in order to allow ControllerValidationHandlers to be used in place of BindingResults
        //
        boolean hasBindingResult = (paramTypes.length > (i + 1) && Errors.class.isAssignableFrom(paramTypes[i + 1])
            || ControllerValidationHandler.class.isAssignableFrom(paramTypes[i + 1]));

        return !hasBindingResult;
    }
}
