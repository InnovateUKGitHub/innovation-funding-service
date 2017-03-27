package org.innovateuk.ifs.agreement.service;

import org.innovateuk.ifs.user.resource.AgreementResource;
import org.innovateuk.ifs.user.service.AgreementRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains methods to retrieve and store {@link AgreementResource} related data,
 * through the RestService {@link AgreementRestService}.
 */
@Service
public class AgreementServiceImpl implements AgreementService {

    @Autowired
    private AgreementRestService agreementRestService;

    @Override
    public AgreementResource getCurrentAgreement() {
        return agreementRestService.getCurrentAgreement().getSuccessObjectOrThrowException();
    }
}
