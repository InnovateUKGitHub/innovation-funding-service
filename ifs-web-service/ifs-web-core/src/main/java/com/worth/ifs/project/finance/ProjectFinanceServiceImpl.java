package com.worth.ifs.project.finance;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.service.ProjectFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * A service for dealing with a Project's finance operations
 */
@Service
public class ProjectFinanceServiceImpl implements ProjectFinanceService {

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Override
    public ServiceResult<Void> generateSpendProfile(Long projectId, Long partnerOrganisationId) {
        return projectFinanceRestService.generateSpendProfile(projectId, partnerOrganisationId);
    }
}
