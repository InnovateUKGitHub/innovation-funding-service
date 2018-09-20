package org.innovateuk.ifs.review.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.security.CompetitionParticipantLookupStrategy;
import org.innovateuk.ifs.assessment.security.CompetitionParticipantPermissionRules;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.ReviewParticipantResource;
import org.innovateuk.ifs.review.transactional.ReviewInviteService;
import org.innovateuk.ifs.review.transactional.ReviewInviteServiceImpl;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.security.UserLookupStrategies;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.review.builder.ReviewParticipantResourceBuilder.newReviewParticipantResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.mockito.Mockito.*;

public class ReviewInviteServiceSecurityTest extends BaseServiceSecurityTest<ReviewInviteService> {

    private CompetitionParticipantPermissionRules competitionParticipantPermissionRules;
    private ReviewInvitePermissionRules reviewInvitePermissionRules;
    private CompetitionParticipantLookupStrategy competitionParticipantLookupStrategy;
    private UserLookupStrategies userLookupStrategies;
    private ReviewParticipantPermissionRules reviewParticipantPermissionRules;
    private ReviewParticipantLookupStrategy reviewParticipantLookupStrategy;

    @Override
    protected Class<? extends ReviewInviteService> getClassUnderTest() {
        return ReviewInviteServiceImpl.class;
    }

    @Before
    public void setUp() throws Exception {
        competitionParticipantPermissionRules = getMockPermissionRulesBean(CompetitionParticipantPermissionRules.class);
        competitionParticipantLookupStrategy = getMockPermissionEntityLookupStrategiesBean
                (CompetitionParticipantLookupStrategy.class);
        reviewInvitePermissionRules = getMockPermissionRulesBean(ReviewInvitePermissionRules.class);
        userLookupStrategies = getMockPermissionEntityLookupStrategiesBean(UserLookupStrategies.class);
        reviewParticipantPermissionRules = getMockPermissionRulesBean(ReviewParticipantPermissionRules.class);
        reviewParticipantLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ReviewParticipantLookupStrategy
                .class);
    }

    @Test
    public void getCreatedInvites() {
        Pageable pageable = new PageRequest(0, 20);

        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getCreatedInvites(1L, pageable),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void inviteExistingUsers() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.inviteUsers(
                newExistingUserStagedInviteResource().build(2)), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        Pageable pageable = new PageRequest(0, 20);

        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAvailableAssessors(1L, pageable),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getAvailableAssessorIds() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAvailableAssessorIds(1L),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getNonAcceptedAssessorInviteIds() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getNonAcceptedAssessorInviteIds(1L),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getAllInvitesToSend() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAllInvitesToSend(1L),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void sendAllInvites() throws Exception {
        AssessorInviteSendResource assessorInviteSendResource = new AssessorInviteSendResource();
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.sendAllInvites(1L, assessorInviteSendResource),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getResendInvites() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAllInvitesToResend(1L, singletonList(2L)),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void resendInvites() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.resendInvites(singletonList(2L),
                newAssessorInviteSendResource().build()), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getInvitationOverview() {
        Pageable pageable = new PageRequest(0, 20);
        List<ParticipantStatus> statuses = asList(ParticipantStatus.PENDING, ParticipantStatus.REJECTED);

        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getInvitationOverview(1L, pageable, statuses),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getAllInvitesByUser() throws Exception {
        UserResource assessorUserResource = newUserResource()
                .withRolesGlobal(singletonList(
                        Role.ASSESSOR
                ))
                .build();

        when(classUnderTestMock.getAllInvitesByUser(1L))
                .thenReturn(serviceSuccess(singletonList(newReviewParticipantResource().withUser(1L).build())));
        when(userLookupStrategies.findById(1L)).thenReturn(assessorUserResource);

        assertAccessDenied(
                () -> classUnderTest.getAllInvitesByUser(1L),
                () -> {
                    verify(reviewInvitePermissionRules).userCanViewInvites(isA(UserResource.class), isA(UserResource
                            .class));
                    verifyNoMoreInteractions(reviewInvitePermissionRules);
                }
        );
    }

    @Test
    public void acceptInvite() {
        UserResource assessorUserResource = newUserResource()
                .withRolesGlobal(singletonList(
                        Role.ASSESSOR
                        )
                ).build();
        ReviewParticipantResource reviewParticipantResource = newReviewParticipantResource().build();

        when(reviewParticipantLookupStrategy.getAssessmentPanelParticipantResource("hash"))
                .thenReturn(reviewParticipantResource);
        when(reviewParticipantPermissionRules.userCanAcceptAssessmentPanelInvite(reviewParticipantResource,
                assessorUserResource))
                .thenReturn(true);

        setLoggedInUser(assessorUserResource);

        classUnderTest.acceptInvite("hash");

        verify(reviewParticipantLookupStrategy, only()).getAssessmentPanelParticipantResource("hash");
        verify(reviewParticipantPermissionRules, only()).userCanAcceptAssessmentPanelInvite
                (reviewParticipantResource, assessorUserResource);
    }

    @Test
    public void acceptInvite_notLoggedIn() {
        setLoggedInUser(null);
        assertAccessDenied(
                () -> classUnderTest.acceptInvite("hash"),
                () -> {
                    verify(reviewParticipantLookupStrategy, only()).getAssessmentPanelParticipantResource("hash");
                    verifyZeroInteractions(reviewParticipantPermissionRules);
                }
        );
    }

    @Test
    public void acceptInvite_notSameUser() {
        UserResource assessorUserResource = newUserResource()
                .withRolesGlobal(singletonList(
                        Role.ASSESSOR
                        )
                ).build();
        ReviewParticipantResource reviewParticipantResource = newReviewParticipantResource().build();
        when(reviewParticipantLookupStrategy.getAssessmentPanelParticipantResource("hash"))
                .thenReturn(reviewParticipantResource);
        when(reviewParticipantPermissionRules.userCanAcceptAssessmentPanelInvite(reviewParticipantResource,
                assessorUserResource))
                .thenReturn(false);

        setLoggedInUser(assessorUserResource);

        assertAccessDenied(
                () -> classUnderTest.acceptInvite("hash"),
                () -> {
                    verify(reviewParticipantLookupStrategy, only()).getAssessmentPanelParticipantResource("hash");
                    verify(reviewParticipantPermissionRules, only()).userCanAcceptAssessmentPanelInvite
                            (reviewParticipantResource, assessorUserResource);
                }
        );
    }

    @Test
    public void acceptInvite_hashNotExists() {
        UserResource assessorUserResource = newUserResource()
                .withRolesGlobal(singletonList(
                        Role.ASSESSOR
                        )
                ).build();

        when(reviewParticipantLookupStrategy.getAssessmentPanelParticipantResource("hash not exists")).thenReturn(null);

        setLoggedInUser(assessorUserResource);

        assertAccessDenied(
                () -> classUnderTest.acceptInvite("hash not exists"),
                () -> {
                    verify(reviewParticipantLookupStrategy, only()).getAssessmentPanelParticipantResource("hash not " +
                            "exists");
                    verifyZeroInteractions(reviewParticipantPermissionRules);
                }
        );
    }

    @Test
    public void deleteInvite() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.deleteInvite("email", 1L), COMP_ADMIN,
                PROJECT_FINANCE);
    }

    @Test
    public void deleteAllInvites() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.deleteAllInvites(1L), COMP_ADMIN, PROJECT_FINANCE);
    }
}
