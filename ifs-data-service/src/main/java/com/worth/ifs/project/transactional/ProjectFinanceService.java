package com.worth.ifs.project.transactional;

import com.worth.ifs.commons.service.ServiceResult;

/**
 * Service dealing with Project finance operations
 */
public interface ProjectFinanceService {

    ServiceResult<Void> generateSpendProfile(Long projectId, Long partnerOrganisationId);
}
