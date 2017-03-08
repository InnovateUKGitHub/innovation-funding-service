package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implements methods for setting an Applications research category and retrieving available choices.
 */
@Service
public class ApplicationResearchCategoryRestServiceImpl extends BaseRestService implements ApplicationResearchCategoryRestService {
    private String applicationResearchCategoryRestURL = "/applicationResearchCategory/";

    @Override
    public RestResult<ApplicationResource> saveApplicationResearchCategoryChoice(Long applicationId, Long researchCategoryId) {
        return postWithRestResult(applicationResearchCategoryRestURL + "researchCategory/" + applicationId, researchCategoryId, ApplicationResource.class);
    }

    @Override
    public RestResult<List<ResearchCategoryResource>> getAvailableResearchCategoriesForApplication(Long applicationId) {
        return getWithRestResult(applicationResearchCategoryRestURL + "availableResearchCategories/" + applicationId, ParameterizedTypeReferences.researchCategoryResourceListType());
    }
}
