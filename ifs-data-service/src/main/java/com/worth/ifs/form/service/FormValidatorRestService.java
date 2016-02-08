package com.worth.ifs.form.service;

import com.worth.ifs.form.resource.FormValidatorResource;

public interface FormValidatorRestService {
    FormValidatorResource findOne(Long id);
}