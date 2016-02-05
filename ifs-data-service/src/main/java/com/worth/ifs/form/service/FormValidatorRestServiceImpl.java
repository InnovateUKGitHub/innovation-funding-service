package com.worth.ifs.form.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.form.resource.FormValidatorResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FormValidatorRestServiceImpl extends BaseRestService implements FormValidatorRestService {
    @Value("${ifs.data.service.rest.formvalidator}")
    private String restUrl;


    @Override
    public FormValidatorResource findOne(Long id) {
        return restGet(restUrl + "/" + id, FormValidatorResource.class);
    }
}