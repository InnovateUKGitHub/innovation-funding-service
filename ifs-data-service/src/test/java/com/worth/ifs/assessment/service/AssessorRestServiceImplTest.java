package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import org.junit.Test;

import static com.worth.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static java.lang.String.format;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class AssessorRestServiceImplTest extends BaseRestServiceUnitTest<AssessorRestServiceImpl> {
    private static final String assessorRestUrl = "/assessor";

    @Override
    protected AssessorRestServiceImpl registerRestServiceUnderTest() {
        AssessorRestServiceImpl assessorRestService = new AssessorRestServiceImpl();
        assessorRestService.setAssessorRestUrl(assessorRestUrl);
        return assessorRestService;
    }

    @Test
    public void createAssessorByInviteHash() throws Exception {
        String hash = "testhash";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource().build();
        setupPostWithRestResultAnonymousExpectations(format("%s/register/%s", assessorRestUrl, hash), Void.class, userRegistrationResource, null, OK);

        RestResult<Void> response = service.createAssessorByInviteHash(hash, userRegistrationResource);
        assertTrue(response.isSuccess());
    }

}