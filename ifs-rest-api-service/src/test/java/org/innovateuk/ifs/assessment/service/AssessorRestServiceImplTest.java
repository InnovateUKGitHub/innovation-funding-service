package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static java.lang.Boolean.TRUE;
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
    public void createAssessorByInviteHash() {
        String hash = "testhash";

        UserRegistrationResource userRegistrationResource = new UserRegistrationResource();
        setupPostWithRestResultAnonymousExpectations(format("%s/register/%s", assessorRestUrl, hash), Void.class,
                userRegistrationResource, null, OK);

        RestResult<Void> response = service.createAssessorByInviteHash(hash, userRegistrationResource);
        assertTrue(response.isSuccess());
    }

    @Test
    public void hasApplicationsAssigned() {

        long userId = 1l;

        setupGetWithRestResultAnonymousExpectations(format("%s/applications-assigned/%s", assessorRestUrl, userId), Boolean.class, TRUE);

        RestResult<Boolean> response = service.hasApplicationsAssigned(userId);
        assertTrue(response.isSuccess());
    }


    @Test
    public void notifyAssessors() {
        long competitionId = 1L;

        setupPutWithRestResultExpectations(format("%s/notify-assessors/competition/%s", assessorRestUrl,
                competitionId), HttpStatus.OK);

        RestResult<Void> result = service.notifyAssessors(competitionId);
        assertTrue(result.isSuccess());
    }

}
