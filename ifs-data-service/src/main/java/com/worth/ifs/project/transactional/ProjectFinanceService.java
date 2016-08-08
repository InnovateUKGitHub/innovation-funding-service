package com.worth.ifs.project.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;

/**
 * Service dealing with Project finance operations
 */
public interface ProjectFinanceService {

    @NotSecured(value = "TODO DW - to be secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> generateSpendProfile(Long projectId);
}
