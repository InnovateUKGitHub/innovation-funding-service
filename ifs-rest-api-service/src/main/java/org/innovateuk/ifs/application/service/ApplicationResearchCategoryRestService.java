package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.rest.RestResult;

/**
 * Interface providing the actions for changing an application research category / retrieving available research categoriess.
 */
public interface ApplicationResearchCategoryRestService {
    RestResult<ApplicationResource> saveApplicationResearchCategoryChoice(Long applicationId, Long researchCategoryId);
}
