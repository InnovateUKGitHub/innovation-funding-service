package com.worth.ifs.form.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.resource.FormValidatorResource;

public interface FormValidatorRestService {

    RestResult<FormValidatorResource> findOne(Long id);
}