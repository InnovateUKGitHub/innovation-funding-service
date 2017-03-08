package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Interface providing the actions for changing an application research category / retrieving available research categoriess.
 */
public interface ApplicationResearchCategoryRestService {
    RestResult<ApplicationResource> saveApplicationResearchCategoryChoice(Long applicationId, Long researchCategoryId);
    RestResult<List<ResearchCategoryResource>> getAvailableResearchCategoriesForApplication(Long applicationId);
}
