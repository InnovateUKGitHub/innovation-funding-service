package com.worth.ifs.form.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.resource.FormInputTypeResource;

public interface FormInputTypeRestService {
    RestResult<FormInputTypeResource> findOne(Long id);
}