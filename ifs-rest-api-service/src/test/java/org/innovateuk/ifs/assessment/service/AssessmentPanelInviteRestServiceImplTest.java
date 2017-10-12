package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.invite.resource.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInvitePageResourceBuilder.newAssessorCreatedInvitePageResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder.newAssessorInviteOverviewPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorPageResourceBuilder.newAvailableAssessorPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder.newCompetitionInviteStatisticsResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteListResourceBuilder.newExistingUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteListResourceBuilder.newNewUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class AssessmentPanelInviteRestServiceImplTest extends BaseRestServiceUnitTest<AssessmentPanelInviteRestServiceImpl> {

    private static final String restUrl = "/assessmentpanelinvite";

    @Override
    protected AssessmentPanelInviteRestServiceImpl registerRestServiceUnderTest() {
        AssessmentPanelInviteRestServiceImpl assessmentPanelInviteRestService = new AssessmentPanelInviteRestServiceImpl();
        return assessmentPanelInviteRestService;
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        long competitionId = 1L;
        int page = 1;

        List<AvailableAssessorResource> assessorItems = newAvailableAssessorResource().build(2);

        AvailableAssessorPageResource expected = newAvailableAssessorPageResource()
                .withContent(assessorItems)
                .build();

        setupGetWithRestResultExpectations(
                format("%s/%s/%s?page=1", restUrl, "getAvailableAssessors", competitionId),
                AvailableAssessorPageResource.class,
                expected
        );

        AvailableAssessorPageResource actual = service.getAvailableAssessors(competitionId, page).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void getAvailableAssessorIds() throws Exception {
        long competitionId = 1L;

        List<Long> assessorItems = asList(1L, 2L);

        setupGetWithRestResultExpectations(
                format("%s/%s/%s", restUrl, "getAvailableAssessorIds", competitionId),
                ParameterizedTypeReferences.longsListType(),
                assessorItems
        );

        List<Long> actual = service.getAvailableAssessorIds(competitionId).getSuccessObject();
        assertEquals(assessorItems, actual);
    }

    @Test
    public void getCreatedInvites() throws Exception {
        long competitionId = 1L;
        int page = 1;
        AssessorCreatedInvitePageResource expected = newAssessorCreatedInvitePageResource()
                .withContent(newAssessorCreatedInviteResource().build(2))
                .build();

        setupGetWithRestResultExpectations(format("%s/%s/%s?page=1", restUrl, "getCreatedInvites", competitionId), AssessorCreatedInvitePageResource.class, expected);

        AssessorCreatedInvitePageResource actual = service.getCreatedInvites(competitionId, page).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void inviteUsers() throws Exception {
        long competitionId = 1L;

        ExistingUserStagedInviteListResource existingUserStagedInviteListResource = newExistingUserStagedInviteListResource()
                .withInvites(
                        newExistingUserStagedInviteResource()
                                .withUserId(1L, 2L)
                                .withCompetitionId(competitionId)
                                .build(2)
                )
                .build();

        setupPostWithRestResultExpectations(format("%s/%s", restUrl, "inviteUsers"), existingUserStagedInviteListResource, OK);

        RestResult<Void> restResult = service.inviteUsers(existingUserStagedInviteListResource);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void sendAllInvites() {
        long competitionId = 1L;
        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "sendAllInvites", competitionId), assessorInviteSendResource, OK);
        assertTrue(service.sendAllInvites(competitionId, assessorInviteSendResource).isSuccess());
        setupPostWithRestResultVerifications(format("%s/%s/%s", restUrl, "sendAllInvites", competitionId), Void.class, assessorInviteSendResource);
    }

    @Test
    public void getAllInvitesToSend() throws Exception {
        long competitionId = 1L;

        AssessorInvitesToSendResource expected = newAssessorInvitesToSendResource()
                .withRecipients(asList("James", "John"))
                .build();

        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "getAllInvitesToSend", competitionId), AssessorInvitesToSendResource.class, expected);
        AssessorInvitesToSendResource actual = service.getAllInvitesToSend(competitionId).getSuccessObject();
        assertEquals(expected, actual);
    }

}
