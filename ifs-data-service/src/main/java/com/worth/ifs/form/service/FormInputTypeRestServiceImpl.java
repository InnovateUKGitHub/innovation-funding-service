package com.worth.ifs.form.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.form.resource.FormInputTypeResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FormInputTypeRestServiceImpl extends BaseRestService implements FormInputTypeRestService {

    @Value("${ifs.data.service.rest.forminputtype}")
    private String restUrl;


    @Override
    public RestResult<FormInputTypeResource> findOne(Long id) {
        return getWithRestResult(restUrl + "/" + id, FormInputTypeResource.class);
    }
}