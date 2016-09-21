package com.worth.ifs.assessment.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class AssessorRestServiceImpl extends BaseRestService implements AssessorRestService {
    private static final String registerUserByHashUrl = "/register";

    @Override
    public RestResult<Void> createAssessorByInviteHash(String hash, UserRegistrationResource userRegistrationResource) {
        return postWithRestResultAnonymous(format("%s/%s/%s", registerUserByHashUrl, hash), userRegistrationResource, Void.class);
    }
}
