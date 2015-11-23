package com.worth.ifs.form;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.service.ResponseRestService;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.service.FormInputResponseRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<Long, FormInputResponse> mapFormInputResponsesToFormInput(List<FormInputResponse> responses) {
        Map<Long, FormInputResponse> responseMap = new HashMap<>();
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
