package com.worth.ifs.controller;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * An object that can be used to store BindingErrors for the purposes of Spring Forms and Thymeleaf processing
 */
public interface BindingResultTarget {

    BindingResult getBindingResult();

    void setBindingResult(BindingResult bindingResult);

    List<ObjectError> getObjectErrors();

    void setObjectErrors(List<ObjectError> errors);
}
