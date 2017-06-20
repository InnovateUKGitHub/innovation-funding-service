package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.EthnicityResource;

import java.util.List;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.user.domain.Ethnicity} data.
 */
public interface EthnicityService {

    @NotSecured(value = "Anyone can view all active ethnicities", mustBeSecuredByOtherServices = false)
    ServiceResult<List<EthnicityResource>> findAllActive();
}
