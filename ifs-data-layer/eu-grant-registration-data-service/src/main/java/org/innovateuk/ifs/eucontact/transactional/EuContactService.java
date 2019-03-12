package org.innovateuk.ifs.eucontact.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuContactPageResource;
import org.springframework.data.domain.Pageable;

/**
 * Service for handling eu contacts
 */
public interface EuContactService {

    ServiceResult<EuContactPageResource> getEuContactsByNotified(boolean notified, Pageable pageable);
}