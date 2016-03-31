package com.worth.ifs.form.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.resource.FormInputResource;

public interface FormInputRestService {
    RestResult<FormInputResource> getById(Long id);
}
