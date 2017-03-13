package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.AgreementResource;
import org.springframework.stereotype.Service;

/**
 * AgreementRestServiceImpl is a utility for CRUD operations on {@link AgreementResource}.
 * This class connects to the {@link org.innovateuk.ifs.user.controller.AgreementController}
 * through a REST call.
 */
@Service
public class AgreementRestServiceImpl extends BaseRestService implements AgreementRestService {
    private String agreementRestURL = "/agreement";

    @Override
    public RestResult<AgreementResource> getCurrentAgreement() {
        return getWithRestResult(agreementRestURL + "/findCurrent", AgreementResource.class);
    }
}
