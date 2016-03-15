package com.worth.ifs.form.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.domain.FormInput;

public interface FormInputRestService {
    RestResult<FormInput> getById(Long id);
}
