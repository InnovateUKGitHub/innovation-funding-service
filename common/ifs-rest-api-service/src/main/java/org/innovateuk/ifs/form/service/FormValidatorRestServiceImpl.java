package org.innovateuk.ifs.form.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.form.resource.FormValidatorResource;
import org.springframework.stereotype.Service;

@Service
public class FormValidatorRestServiceImpl extends BaseRestService implements FormValidatorRestService {

    private String restUrl = "/formvalidator";

    @Override
    public RestResult<FormValidatorResource> findOne(Long id) {
        return getWithRestResult(restUrl + "/" + id, FormValidatorResource.class);
    }
}
