package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.Optional;

public interface ApplicationEoiEvidenceResponseRestService {

    RestResult<Optional<ApplicationEoiEvidenceResponseResource>> findOneByApplicationId(long applicationId);
}
