package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implements methods for setting an Applications innovation area and retrieving available choices.
 */
@Service
public class ApplicationInnovationAreaRestServiceImpl extends BaseRestService implements ApplicationInnovationAreaRestService {
    private String applicationFundingDecisionRestURL = "/applicationInnovationArea/";

    @Override
    public RestResult<ApplicationResource> saveApplicationInnovationAreaChoice(Long applicationId, Long innovationAreaId) {
        return postWithRestResult(applicationFundingDecisionRestURL + "innovationArea/" + applicationId, innovationAreaId, ApplicationResource.class);
    }

    @Override
    public RestResult<ApplicationResource> setApplicationInnovationAreaToNotApplicable(Long applicationId) {
        return postWithRestResult(applicationFundingDecisionRestURL + "noInnovationAreaApplicable/" + applicationId, ApplicationResource.class);
    }

    @Override
    public RestResult<List<InnovationAreaResource>> getAvailableInnovationAreasForApplication(Long applicationId) {
        return getWithRestResult(applicationFundingDecisionRestURL + "availableInnovationAreas/" + applicationId, ParameterizedTypeReferences.innovationAreaResourceListType());
    }
}
