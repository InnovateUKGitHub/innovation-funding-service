package org.innovateuk.ifs.form.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.resource.FormValidatorResource;

public interface FormValidatorRestService {

    RestResult<FormValidatorResource> findOne(Long id);
}
