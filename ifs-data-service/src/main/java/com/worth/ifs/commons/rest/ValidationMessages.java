package com.worth.ifs.commons.rest;

import com.worth.ifs.commons.error.Error;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Resource object to return validation messages on rest calls.
 */
public class ValidationMessages implements Serializable {
    private String objectName;
    private Long objectId;
    private List<Error> errors;

    public ValidationMessages(Long objectId, BindingResult bindingResult) {
        errors = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(e->{
            List<Object> args = Arrays.asList();
            String errorMessage = e.getDefaultMessage();
            if(StringUtils.isEmpty(errorMessage)){
                errorMessage = e.getCode();
            }
            Error error = new Error(e.getField(), errorMessage, args, HttpStatus.NOT_ACCEPTABLE);
            errors.add(error);
            }
        );

        bindingResult.getGlobalErrors().forEach(e->{
                    List<Object> args = Arrays.asList();
                    Error error = new Error("", e.getDefaultMessage(), args, HttpStatus.NOT_ACCEPTABLE);
                    errors.add(error);
                }
        );
        objectName = bindingResult.getObjectName();
        this.objectId = objectId;
    }

    public ValidationMessages() {

    }

    public String getObjectName() {
        return objectName;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
