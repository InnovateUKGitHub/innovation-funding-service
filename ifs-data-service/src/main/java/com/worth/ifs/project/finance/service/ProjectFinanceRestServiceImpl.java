package com.worth.ifs.project.finance.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.stereotype.Service;

/**
 * Rest Service for dealing with Project finance operations
 */
@Service
public class ProjectFinanceRestServiceImpl extends BaseRestService implements ProjectFinanceRestService {

    private String projectFinanceRestURL = "/project";

    @Override
    public ServiceResult<Void> generateSpendProfile(Long projectId) {
        String url = projectFinanceRestURL + "/" + projectId + "/spend-profile/generate";
        return postWithRestResult(url, Void.class).toServiceResult();
    }
}
