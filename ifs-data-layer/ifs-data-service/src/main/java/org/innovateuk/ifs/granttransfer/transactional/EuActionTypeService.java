package org.innovateuk.ifs.granttransfer.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;

import java.util.List;

/**
 * Service for retreiving eu action types.
 */
public interface EuActionTypeService {
    ServiceResult<List<EuActionTypeResource>> findAll();
    ServiceResult<EuActionTypeResource> getById(long id);
}
