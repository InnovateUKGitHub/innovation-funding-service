package com.worth.ifs.controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

/**
 * Custom advice for adding additional Spring form binding configuration to any Controllers that require it
 */
@ControllerAdvice
public class CustomFormBindingControllerAdvice {

    @InitBinder
    public void registerCustomEditors(WebDataBinder binder, WebRequest request) {
        binder.registerCustomEditor(LocalDate.class, new LocalDatePropertyEditor(request));
    }
}
