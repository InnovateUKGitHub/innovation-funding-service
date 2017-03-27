package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Interface providing the actions for changing application funding decisions / retrieving available funding decisions.
 */
public interface ApplicationInnovationAreaRestService {
    RestResult<ApplicationResource> saveApplicationInnovationAreaChoice(Long applicationId, Long innovationAreaId);
    RestResult<ApplicationResource> setApplicationInnovationAreaToNotApplicable(Long applicationId);
    RestResult<List<InnovationAreaResource>> getAvailableInnovationAreasForApplication(Long applicationId);
}
