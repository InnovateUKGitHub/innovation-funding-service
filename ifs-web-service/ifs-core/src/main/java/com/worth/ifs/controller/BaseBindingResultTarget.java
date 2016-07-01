package com.worth.ifs.controller;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of BindingResultTarget with basic properties, getters and setters
 */
public class BaseBindingResultTarget implements BindingResultTarget {

    private List<ObjectError> objectErrors = new ArrayList<>();
    private BindingResult bindingResult;

    @Override
    public List<ObjectError> getObjectErrors() {
        return objectErrors;
    }

    @Override
    public void setObjectErrors(List<ObjectError> objectErrors) {
        this.objectErrors = objectErrors;
    }

    @Override
    public BindingResult getBindingResult() {
        return bindingResult;
    }

    @Override
    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }
}
