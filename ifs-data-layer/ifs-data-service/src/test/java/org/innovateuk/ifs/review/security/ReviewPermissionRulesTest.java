package org.innovateuk.ifs.review.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.mapper.ReviewMapper;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.review.builder.ReviewBuilder.newReview;
import static org.innovateuk.ifs.review.builder.ReviewResourceBuilder.newReviewResource;
import static org.innovateuk.ifs.review.resource.ReviewState.ACCEPTED;
import static org.innovateuk.ifs.review.resource.ReviewState.PENDING;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ReviewPermissionRulesTest extends BasePermissionRulesTest<ReviewPermissionRules> {

    private UserResource assessorUser;
    private UserResource otherUser;
    private Map<ReviewState, ReviewResource> assessmentReviews;

    @Mock
    private ReviewRepository reviewRepositoryMock;

    @Autowired
    public ReviewMapper reviewMapper;

    @Override
    protected ReviewPermissionRules supplyPermissionRulesUnderTest() {
        return new ReviewPermissionRules();
    }

    @Before
    public void setup() {
        assessorUser = newUserResource().build();
        otherUser = newUserResource().build();

        ProcessRole processRole = newProcessRole()
                .withUser(newUser().with(id(assessorUser.getId())).build())
                .build();
        when(processRoleRepositoryMock.findById(processRole.getId())).thenReturn(Optional.of(processRole));

        assessmentReviews = EnumSet.allOf(ReviewState.class).stream().collect(toMap(identity(), state -> setupAssessmentReview(processRole, state)));
    }

    @Test
    public void ownersCanReadAssessmentsOnDashboard() {
        EnumSet<ReviewState> allowedStates = EnumSet.of(PENDING, ACCEPTED);

        allowedStates.forEach(state ->
                assertTrue("the owner of an assessment review should be able to read that assessment review on the dashboard",
                        rules.userCanReadAssessmentReviewOnDashboard(assessmentReviews.get(state), assessorUser)));

        EnumSet.complementOf(allowedStates).forEach(state ->
                assertFalse("the owner of an assessment review should not be able to read that assessment review on the dashboard",
                        rules.userCanReadAssessmentReviewOnDashboard(assessmentReviews.get(state), assessorUser)));
    }

    @Test
    public void otherUsersCanNotReadAssessmentsOnDashboard() {
        EnumSet.allOf(ReviewState.class).forEach(state ->
                assertFalse("other users should not be able to read any assessment reviews",
                        rules.userCanReadAssessmentReviewOnDashboard(assessmentReviews.get(state), otherUser)));
    }

    private ReviewResource setupAssessmentReview(ProcessRole participant, ReviewState state) {
        Review review = newReview()
                .withState(state)
                .build();

        when(reviewRepositoryMock.findById(review.getId())).thenReturn(Optional.of(review));

        return newReviewResource()
                .withId(review.getId())
                .withProcessRole(participant.getId())
                .build();
    }
}