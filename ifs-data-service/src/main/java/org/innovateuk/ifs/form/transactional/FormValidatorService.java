package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.resource.FormValidatorResource;
import org.innovateuk.ifs.commons.security.NotSecured;

public interface FormValidatorService {

    @NotSecured(value = "everyone is allowed to read the form validators", mustBeSecuredByOtherServices = false)
    ServiceResult<FormValidatorResource> findOne(Long id);
}
