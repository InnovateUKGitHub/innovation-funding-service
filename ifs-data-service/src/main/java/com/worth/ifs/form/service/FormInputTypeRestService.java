package com.worth.ifs.form.service;

import com.worth.ifs.form.resource.FormInputTypeResource;
import com.worth.ifs.security.NotSecured;

public interface FormInputTypeRestService {
    @NotSecured("REST Service")
    FormInputTypeResource findOne(Long id);
}