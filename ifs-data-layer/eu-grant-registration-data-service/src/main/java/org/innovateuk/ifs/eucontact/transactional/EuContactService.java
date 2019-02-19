package org.innovateuk.ifs.eucontact.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuContactPageResource;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EuContactService {

    // not secured
    ServiceResult<EuContactPageResource> getEuContactsByNotified(boolean notified, Pageable pageable);
}