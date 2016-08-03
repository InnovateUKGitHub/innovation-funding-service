package com.worth.ifs.project.finance;

import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * A service for dealing with a Project's finance operations
 */
@Service
public class ProjectFinanceServiceImpl implements ProjectFinanceService {

    @Override
    public ServiceResult<Void> generateSpendProfile(Long projectId, Long partnerOrganisationId) {
        return serviceSuccess();
    }
}
