package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.form.domain.FormInputResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * This class contains methods to retrieve and store {@link Response} related data,
 * through the RestService {@link ResponseRestService}.
 */
@Service
public class FormInputResponseServiceImpl implements FormInputResponseService {

    @Autowired
    FormInputResponseRestService responseRestService;

    @Override
    public List<FormInputResponse> getByApplication(Long applicationId) {
        return responseRestService.getResponsesByApplicationId(applicationId);
    }

    @Override
    public HashMap<Long, FormInputResponse> mapResponsesToQuestion(List<FormInputResponse> responses) {
        HashMap<Long, FormInputResponse> responseMap = new HashMap<>();
        for (FormInputResponse response : responses) {
            responseMap.put(response.getFormInput().getId(), response);
        }
        return responseMap;
    }

    @Override
    public List<String> save(Long userId, Long applicationId, Long formInputId, String value) {
        return responseRestService.saveQuestionResponse(userId, applicationId, formInputId, value);
    }
}
