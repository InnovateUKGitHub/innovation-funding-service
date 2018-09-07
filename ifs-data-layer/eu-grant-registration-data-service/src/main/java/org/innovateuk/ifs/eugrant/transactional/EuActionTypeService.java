package org.innovateuk.ifs.eugrant.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuActionTypeResource;

import java.util.List;
import java.util.UUID;

/**
 * Service for retreiving eu action types.
 */
public interface EuActionTypeService {
    ServiceResult<List<EuActionTypeResource>> findAll();
    ServiceResult<EuActionTypeResource> getById(long id);
}
