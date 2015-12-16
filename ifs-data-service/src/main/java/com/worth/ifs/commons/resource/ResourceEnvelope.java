package com.worth.ifs.commons.resource;

import java.util.List;

public class ResourceEnvelope<T> {
    private String status;
    private List<ResourceError> errors;
    private T entity;

    public ResourceEnvelope() {}

    public ResourceEnvelope(ResourceEnvelope<T> resourceEnvelope) {
        this(resourceEnvelope.getStatus(),
                resourceEnvelope.getErrors(),
                resourceEnvelope.getEntity());
    }

    public ResourceEnvelope(String status, List<ResourceError> errors, T entity) {
        this.status = status;
        this.errors = errors;
        this.entity = entity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ResourceError> getErrors() {
        return errors;
    }

    public void addError(ResourceError error) {
        this.errors.add(error);
    }

    public void setErrors(List<ResourceError> errors) {
        this.errors = errors;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}
