package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.commons.rest.RestResult;

public interface ApplicationEoiEvidenceResponseRestService {

    RestResult<ApplicationEoiEvidenceResponseResource> findOneByApplicationId(long applicationId);
}
