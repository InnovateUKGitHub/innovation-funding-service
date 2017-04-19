package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * A Service that covers basic operations concerning Users
 */
public interface CrmService {

    ServiceResult<Void> syncCrmContact(final long userId);
}
