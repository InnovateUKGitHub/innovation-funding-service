package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;
import java.util.List;

/**
 * ApplicationRestRestService is a utility to use client-side to retrieve Application data from the data-service controllers.
 */
public interface ResponseRestService {
    public List<Response> getResponsesByApplicationId(Long applicationId);
    public Boolean saveQuestionResponse(Long userId, Long applicationId, Long questionId, String value);
}
