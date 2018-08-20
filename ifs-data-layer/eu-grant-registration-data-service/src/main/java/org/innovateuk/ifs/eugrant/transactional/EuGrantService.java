package org.innovateuk.ifs.eugrant.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;

import java.util.UUID;

public interface EuGrantService {

    ServiceResult<EuGrantResource> save(EuGrantResource externalFundingResource);
    ServiceResult<EuGrantResource> get(UUID id);

}
