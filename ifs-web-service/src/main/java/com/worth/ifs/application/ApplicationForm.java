package com.worth.ifs.application;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to setup and submit the form input values. On submit the values are converted into an ApplicationForm object.
 * http://stackoverflow.com/a/4511716
 */
public class ApplicationForm {
    private Map<String, String> formInput;
    public List<ObjectError> objectErrors;
    public BindingResult bindingResult;

    public ApplicationForm() {
        this.formInput = new HashMap<>();
    }

    public Map<String, String> getFormInput() {
        return formInput;
    }

    public void setFormInput(Map<String, String> values) {
        this.formInput = values;
    }
}
