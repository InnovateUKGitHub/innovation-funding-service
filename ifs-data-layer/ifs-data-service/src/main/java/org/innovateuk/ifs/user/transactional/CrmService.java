package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;

/**
 * A Service that covers basic operations concerning CRM data
 */
public interface CrmService {

    @NotSecured(value = "Anyone can update crm", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> syncCrmContact(final long userId);
}
