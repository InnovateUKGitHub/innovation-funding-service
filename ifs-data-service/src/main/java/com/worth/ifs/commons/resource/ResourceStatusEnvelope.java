package com.worth.ifs.commons.resource;

import org.springframework.validation.ObjectError;

import java.util.List;

public class ResourceStatusEnvelope<T> {
    private String status;
    private List<ResourceStatusError> errors;
    private T entity;
    private Class<T> type;

    public ResourceStatusEnvelope() {}

    public ResourceStatusEnvelope(String status, List<ResourceStatusError> errors, T entity) {
        this.status = status;
        this.errors = errors;
        this.entity = entity;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ResourceStatusError> getErrors() {
        return errors;
    }

    public void addError(ResourceStatusError error) {
        this.errors.add(error);
    }

    public void setErrors(List<ResourceStatusError> errors) {
        this.errors = errors;
    }

    public T getEntity() {
        return type.cast(entity);
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}
