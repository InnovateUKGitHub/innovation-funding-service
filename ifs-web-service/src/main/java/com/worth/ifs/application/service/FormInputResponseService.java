package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.form.domain.FormInputResponse;

import java.util.HashMap;
import java.util.List;

/**
 * Interface for CRUD operations on {@link Response} related data.
 */
public interface FormInputResponseService {
    public List<FormInputResponse> getByApplication(Long applicationId);
    public HashMap<Long, FormInputResponse> mapResponsesToQuestion(List<FormInputResponse> responses);
    public List<String> save(Long userId, Long applicationId, Long formInputId, String value);
}
