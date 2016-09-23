package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.security.NotSecured;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.EthnicityResource;

import java.util.List;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.user.domain.Ethnicity} data.
 */
public interface EthnicityService {

    @NotSecured(value = "Anyone can view all active ethnicities", mustBeSecuredByOtherServices = false)
    ServiceResult<List<EthnicityResource>> findAllActive();
}
