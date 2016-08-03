package com.worth.ifs.project.service;

import com.worth.ifs.commons.service.ServiceResult;

/**
 * Rest Service for dealing with Project finance operations
 */
public interface ProjectFinanceRestService {

    ServiceResult<Void> generateSpendProfile(Long projectId, Long partnerOrganisationId);
}
