package com.worth.ifs.project.finance;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.SpendProfileTableResource;

/**
 * A service for dealing with a Project's finance operations
 */
public interface ProjectFinanceService {

    ServiceResult<Void> generateSpendProfile(Long projectId);

    SpendProfileTableResource getSpendProfileTable(Long projectId, Long organisationId);
}
