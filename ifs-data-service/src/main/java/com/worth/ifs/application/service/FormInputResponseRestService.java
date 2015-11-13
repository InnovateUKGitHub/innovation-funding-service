package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.form.domain.FormInputResponse;

import java.util.List;

/**
 * Interface for CRUD operations on {@link Response} related data.
 */
public interface FormInputResponseRestService {
    public List<FormInputResponse> getResponsesByApplicationId(Long applicationId);
    public Boolean saveQuestionResponse(Long userId, Long applicationId, Long formInputId, String value);
}
