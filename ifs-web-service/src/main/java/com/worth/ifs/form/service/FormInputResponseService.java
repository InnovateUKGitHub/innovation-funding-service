package com.worth.ifs.form.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.form.domain.FormInputResponse;

import java.util.List;
import java.util.Map;

/**
 * Interface for CRUD operations on {@link Response} related data.
 */
public interface FormInputResponseService {
    public List<FormInputResponse> getByApplication(Long applicationId);
    public Map<Long, FormInputResponse> mapFormInputResponsesToFormInput(List<FormInputResponse> responses);
    public List<String> save(Long userId, Long applicationId, Long formInputId, String value);
}
