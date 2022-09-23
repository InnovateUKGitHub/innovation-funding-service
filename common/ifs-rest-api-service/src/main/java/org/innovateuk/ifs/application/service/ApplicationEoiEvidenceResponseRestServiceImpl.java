package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationEoiEvidenceResponseRestServiceImpl extends BaseRestService implements ApplicationEoiEvidenceResponseRestService {

    private static String URL = "/application";

    @Override
    public RestResult<Optional<ApplicationEoiEvidenceResponseResource>> findOneByApplicationId(long applicationId) {
        return getWithRestResult(URL + "/" +  applicationId + "/eoi-evidence-response" , ApplicationEoiEvidenceResponseResource.class).toOptionalIfNotFound();
    }
}
