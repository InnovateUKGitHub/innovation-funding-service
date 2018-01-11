package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.assessment.panel.mapper.AssessmentReviewMapper;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState;
import org.innovateuk.ifs.assessment.panel.security.AssessmentReviewPermissionRules;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.EnumSet;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.assessment.builder.AssessmentReviewResourceBuilder.newAssessmentReviewResource;
import static org.innovateuk.ifs.assessment.panel.builder.AssessmentReviewBuilder.newAssessmentReview;
import static org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState.ACCEPTED;
import static org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState.PENDING;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AssessmentReviewPermissionRulesTest extends BasePermissionRulesTest<AssessmentReviewPermissionRules> {

    private UserResource assessorUser;
    private UserResource otherUser;
    private Map<AssessmentReviewState, AssessmentReviewResource> assessmentReviews;

    @Autowired
    public AssessmentReviewMapper assessmentReviewMapper;

    @Override
    protected AssessmentReviewPermissionRules supplyPermissionRulesUnderTest() {
        return new AssessmentReviewPermissionRules();
    }

    @Before
    public void setup() {
        assessorUser = newUserResource().build();
        otherUser = newUserResource().build();

        ProcessRole processRole = newProcessRole()
                .withUser(newUser().with(id(assessorUser.getId())).build())
                .build();
        when(processRoleRepositoryMock.findOne(processRole.getId())).thenReturn(processRole);

        assessmentReviews = EnumSet.allOf(AssessmentReviewState.class).stream().collect(toMap(identity(), state -> setupAssessmentReview(processRole, state)));
    }

    @Test
    public void ownersCanReadAssessmentsOnDashboard() {
        EnumSet<AssessmentReviewState> allowedStates = EnumSet.of(PENDING, ACCEPTED);

        allowedStates.forEach(state ->
                assertTrue("the owner of an assessment review should be able to read that assessment review on the dashboard",
                        rules.userCanReadAssessmentReviewOnDashboard(assessmentReviews.get(state), assessorUser)));

        EnumSet.complementOf(allowedStates).forEach(state ->
                assertFalse("the owner of an assessment review should not be able to read that assessment review on the dashboard",
                        rules.userCanReadAssessmentReviewOnDashboard(assessmentReviews.get(state), assessorUser)));
    }

    @Test
    public void otherUsersCanNotReadAssessmentsOnDashboard() {
        EnumSet.allOf(AssessmentReviewState.class).forEach(state ->
                assertFalse("other users should not be able to read any assessment reviews",
                        rules.userCanReadAssessmentReviewOnDashboard(assessmentReviews.get(state), otherUser)));
    }

    @Test
    public void ownersCanReadAssessmentsNonDashboard() {
        EnumSet<AssessmentReviewState> allowedStates = EnumSet.of(ACCEPTED);

        allowedStates.forEach(state ->
                assertTrue("the owner of an assessment review should be able to read that assessment review",
                        rules.userCanReadAssessmentReview(assessmentReviews.get(state), assessorUser)));

        EnumSet.complementOf(allowedStates).forEach(state ->
                assertFalse("the owner of an assessment review should not be able to read that assessment review",
                        rules.userCanReadAssessmentReview(assessmentReviews.get(state), assessorUser)));
    }

    @Test
    public void otherUsersCanNotReadAssessmentsNonDashboard() {
        EnumSet.allOf(AssessmentReviewState.class).forEach(state ->
                assertFalse("other users should not be able to read any assessment reviews",
                        rules.userCanReadAssessmentReview(assessmentReviews.get(state), otherUser)));
    }

    @Test
    public void ownersCanReadPendingAssessmentsToRespond() {
        EnumSet<AssessmentReviewState> allowedStates = EnumSet.of(PENDING);
        allowedStates.forEach(state ->
                assertTrue("the owner of a pending assessment review should be able to read it in order to respond to their" +
                                " invitation",
                        rules.userCanReadToAssign(assessmentReviews.get(state), assessorUser)));

        EnumSet.complementOf(allowedStates).forEach(state ->
                assertFalse("the owner of an assessment review should not be able to read it in order to respond to" +
                                " their invitation if the assessment review is not pending",
                        rules.userCanReadToAssign(assessmentReviews.get(state), assessorUser)));
    }

    @Test
    public void otherUsersCanNotReadPendingAssessmentsToRespond() {
        EnumSet.allOf(AssessmentReviewState.class).forEach(state ->
            assertFalse("other users should not be able to read assessment reviews in order to respond to invitations",
                    rules.userCanReadToAssign(assessmentReviews.get(state), otherUser)));
    }

    @Test
    public void ownersCanReadAssessmentsToReject() {
        EnumSet<AssessmentReviewState> allowedStates = EnumSet.of(PENDING, ACCEPTED);
        allowedStates.forEach(state ->
                assertTrue("the owner of a pending assessment review should be able to read it in order to respond to their" +
                                " invitation",
                        rules.userCanReadToReject(assessmentReviews.get(state), assessorUser)));

        EnumSet.complementOf(allowedStates).forEach(state ->
                assertFalse("the owner of an assessment review should not be able to read it in order to respond to" +
                                " their invitation if the assessment review is not pending or accepted",
                        rules.userCanReadToReject(assessmentReviews.get(state), assessorUser)));
    }

    @Test
    public void otherUsersCanNotReadAssessmentsToReject() {
        EnumSet.allOf(AssessmentReviewState.class).forEach(state ->
            assertFalse("other users should not be able to read assessment reviews in order to respond to invitations",
                    rules.userCanReadToReject(assessmentReviews.get(state), otherUser)));
    }

    private AssessmentReviewResource setupAssessmentReview(ProcessRole participant, AssessmentReviewState state) {
        AssessmentReview assessmentReview = newAssessmentReview()
                .withState(state)
                .build();

        when(assessmentReviewRepositoryMock.findOne(assessmentReview.getId())).thenReturn(assessmentReview);

        return newAssessmentReviewResource()
                .withId(assessmentReview.getId())
                .withProcessRole(participant.getId())
                .build();
    }
}
