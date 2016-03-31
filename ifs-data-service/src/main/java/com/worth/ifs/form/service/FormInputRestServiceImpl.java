package com.worth.ifs.form.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.resource.FormInputResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FormInputRestServiceImpl extends BaseRestService implements FormInputRestService {

    @Value("${ifs.data.service.rest.forminput}")
    String formInputRestURL;

    @Override
    public RestResult<FormInputResource> getById(Long formInputId) {
        return getWithRestResult(formInputRestURL + "/" + formInputId, FormInputResource.class);
    }
}
