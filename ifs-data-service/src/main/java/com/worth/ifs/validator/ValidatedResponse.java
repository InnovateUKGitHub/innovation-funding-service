package com.worth.ifs.validator;

import com.worth.ifs.application.domain.Response;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

public class ValidatedResponse {
    private Response response;
    private List<String> allErrors;
    private int errorCount;

    public ValidatedResponse() {
    }

    public ValidatedResponse(BindingResult result, Response response) {
        errorCount = result.getErrorCount();
        allErrors = result.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public List<String> getAllErrors() {
        return allErrors;
    }

    public void setAllErrors(List<String> allErrors) {
        this.allErrors = allErrors;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
}
