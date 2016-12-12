package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class AssessorRestServiceImpl extends BaseRestService implements AssessorRestService {
    private String assessorRestUrl = "/assessor";

    protected void setAssessorRestUrl(final String assessorRestUrl) {
        this.assessorRestUrl = assessorRestUrl;
    }

    @Override
    public RestResult<Void> createAssessorByInviteHash(String hash, UserRegistrationResource userRegistrationResource) {
        return postWithRestResultAnonymous(format("%s/register/%s", assessorRestUrl, hash), userRegistrationResource, Void.class);
    }
}
