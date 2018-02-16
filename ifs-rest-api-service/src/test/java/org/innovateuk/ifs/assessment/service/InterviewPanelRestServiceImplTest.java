package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationPageResource;
import org.junit.Test;

import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static java.lang.String.format;
import static org.innovateuk.ifs.invite.builder.AvailableApplicationPageResourceBuilder.newAvailableApplicationPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableApplicationResourceBuilder.newAvailableApplicationResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteListResourceBuilder.newExistingUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.InterviewPanelStagedApplicationPageResourceBuilder.newInterviewPanelStagedApplicationPageResource;
import static org.innovateuk.ifs.invite.builder.InterviewPanelCreatedInviteResourceBuilder.newInterviewPanelStagedApplicationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class InterviewPanelRestServiceImplTest extends BaseRestServiceUnitTest<InterviewPanelRestServiceImpl> {

    private static final String REST_URL = "/interview-panel";

    @Override
    protected InterviewPanelRestServiceImpl registerRestServiceUnderTest() {
        return new InterviewPanelRestServiceImpl();
    }

    @Test
    public void getAvailableApplications() {
        long competitionId = 1L;
        int page = 1;

        AvailableApplicationPageResource expected = newAvailableApplicationPageResource()
                .withContent(newAvailableApplicationResource().build(2))
                .build();

        setupGetWithRestResultExpectations(
                format("%s/%s/%s?page=1", REST_URL, "available-applications", competitionId),
                AvailableApplicationPageResource.class,
                expected
        );

        AvailableApplicationPageResource actual = service.getAvailableApplications(competitionId, page).getSuccess();
        assertEquals(expected, actual);
    }

    @Test
    public void getAvailableApplicationIds() {
        long competitionId = 1L;
        List<Long> expected = asList(1L, 2L);

        setupGetWithRestResultExpectations(
                format("%s/%s/%s", REST_URL, "available-application-ids", competitionId),
                ParameterizedTypeReferences.longsListType(),
                expected
        );

        List<Long> actual = service.getAvailableApplicationIds(competitionId).getSuccess();
        assertEquals(expected, actual);

    }

    @Test
    public void assignApplications() {
        long competitionId = 1L;

        ExistingUserStagedInviteListResource existingUserStagedInviteListResource = newExistingUserStagedInviteListResource()
                .withInvites(
                        newExistingUserStagedInviteResource()
                                .withUserId(1L, 2L)
                                .withCompetitionId(competitionId)
                                .build(2)
                )
                .build();

        setupPostWithRestResultExpectations(format("%s/%s", REST_URL, "assign-applications"), existingUserStagedInviteListResource, OK);

        RestResult<Void> restResult = service.assignApplications(existingUserStagedInviteListResource);
        assertTrue(restResult.isSuccess());

    }

    @Test
    public void getStagedApplications() {
        long competitionId = 1L;
        int page = 1;
        InterviewPanelStagedApplicationPageResource expected = newInterviewPanelStagedApplicationPageResource()
                .withContent(newInterviewPanelStagedApplicationResource().build(2))
                .build();

        setupGetWithRestResultExpectations(format("%s/%s/%s?page=1", REST_URL, "staged-applications", competitionId), InterviewPanelStagedApplicationPageResource.class, expected);

        InterviewPanelStagedApplicationPageResource actual = service.getStagedApplications(competitionId, page).getSuccess();
        assertEquals(expected, actual);
    }
}