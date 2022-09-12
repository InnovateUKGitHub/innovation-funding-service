package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class ApplicationEoiEvidenceResponseRestServiceImpl extends BaseRestService implements ApplicationEoiEvidenceResponseRestService {

    private String applicationEoiEvidenceResponse = "/application-eoi-evidence";

    @Override
    public RestResult<Void> delete(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource) {
        return deleteWithRestResult(format("%s/%d", applicationEoiEvidenceResponse, applicationEoiEvidenceResponseResource.getFileEntryId()), Void.class);
    }
}
