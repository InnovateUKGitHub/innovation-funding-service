package com.worth.ifs.form.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.resource.FormValidatorResource;
import com.worth.ifs.commons.security.NotSecured;

public interface FormValidatorService {

    @NotSecured(value = "everyone is allowed to read the form validators", mustBeSecuredByOtherServices = false)
    ServiceResult<FormValidatorResource> findOne(Long id);
}