package com.worth.ifs.project.service;

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
    public ServiceResult<Void> generateSpendProfile(Long projectId, Long partnerOrganisationId) {
        String url = projectFinanceRestURL + "/" + projectId + "/partner-organisation/" + partnerOrganisationId + "/spend-profile/generate";
        return postWithRestResult(url, Void.class).toServiceResult();
    }
}
