package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.AgreementResource;

/**
 * Interface for CRUD operations on {@link AgreementResource} related data.
 */
public interface AgreementRestService {
    RestResult<AgreementResource> getCurrentAgreement();
}