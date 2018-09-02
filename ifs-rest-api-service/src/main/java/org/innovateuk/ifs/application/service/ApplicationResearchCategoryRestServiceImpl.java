package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * Service implements methods for setting an Applications research category and retrieving available choices.
 */
@Service
public class ApplicationResearchCategoryRestServiceImpl extends BaseRestService implements
        ApplicationResearchCategoryRestService {

    private String applicationResearchCategoryRestURL = "/applicationResearchCategory";

    @Override
    public RestResult<ApplicationResource> setResearchCategory(long applicationId,
                                                               Long researchCategoryId) {
        return postWithRestResult(format("%s/researchCategory/%s", applicationResearchCategoryRestURL, applicationId),
                researchCategoryId, ApplicationResource.class);
    }

    @Override
    public RestResult<ApplicationResource> setResearchCategoryAndMarkAsComplete(long applicationId,
                                                                                long markedAsCompleteById,
                                                                                long researchCategoryId) {
        return postWithRestResult(format("%s/mark-research-category-complete/%s/%s",
                applicationResearchCategoryRestURL, applicationId, markedAsCompleteById), researchCategoryId,
                ApplicationResource.class);
    }
}
