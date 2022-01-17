package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.rest.RestResult;

/**
 * Interface providing the actions for changing an application research category / retrieving available research
 * categories.
 */
public interface ApplicationResearchCategoryRestService {

    RestResult<ApplicationResource> setResearchCategory(long applicationId,
                                                        Long researchCategoryId);

    RestResult<ApplicationResource> setResearchCategoryAndMarkAsComplete(long applicationId,
                                                                         long markedAsCompleteById,
                                                                         long researchCategoryId);
}
