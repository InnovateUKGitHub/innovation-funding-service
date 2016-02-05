package com.worth.ifs.form.service;

import com.worth.ifs.form.resource.FormValidatorResource;
import com.worth.ifs.security.NotSecured;

public interface FormValidatorRestService {
    @NotSecured("REST Service")
    FormValidatorResource findOne(Long id);
}