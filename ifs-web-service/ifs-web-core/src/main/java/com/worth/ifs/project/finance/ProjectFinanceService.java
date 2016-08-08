package com.worth.ifs.project.finance;

import com.worth.ifs.commons.service.ServiceResult;

/**
 * A service for dealing with a Project's finance operations
 */
public interface ProjectFinanceService {

    ServiceResult<Void> generateSpendProfile(Long projectId);
}
