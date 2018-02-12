package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterview;
import org.innovateuk.ifs.assessment.interview.mapper.AssessmentInterviewMapper;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewResource;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState;
import org.innovateuk.ifs.assessment.interview.security.AssessmentInterviewPermissionRules;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.EnumSet;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.assessment.builder.AssessmentInterviewResourceBuilder.newAssessmentInterviewResource;
import static org.innovateuk.ifs.assessment.interview.builder.AssessmentInterviewBuilder.newAssessmentInterview;
import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState.PENDING;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AssessmentInterviewPermissionRulesTest extends BasePermissionRulesTest<AssessmentInterviewPermissionRules> {

    private UserResource assessorUser;
    private UserResource otherUser;
    private Map<AssessmentInterviewState, AssessmentInterviewResource> assessmentInterviews;

    @Autowired
    public AssessmentInterviewMapper assessmentInterviewMapper;

    @Override
    protected AssessmentInterviewPermissionRules supplyPermissionRulesUnderTest() {
        return new AssessmentInterviewPermissionRules();
    }

    @Before
    public void setup() {
        assessorUser = newUserResource().build();
        otherUser = newUserResource().build();

        ProcessRole processRole = newProcessRole()
                .withUser(newUser().with(id(assessorUser.getId())).build())
                .build();
        when(processRoleRepositoryMock.findOne(processRole.getId())).thenReturn(processRole);

        assessmentInterviews = EnumSet.allOf(AssessmentInterviewState.class).stream().collect(toMap(identity(), state -> setupAssessmentInterview(processRole, state)));
    }

    @Test
    public void ownersCanReadAssessmentsOnDashboard() {
        EnumSet<AssessmentInterviewState> allowedStates = EnumSet.of(PENDING);

        allowedStates.forEach(state ->
                assertTrue("the owner of an assessment Interview should be able to read that assessment Interview on the dashboard",
                        rules.userCanReadAssessmentInterviewOnDashboard(assessmentInterviews.get(state), assessorUser)));

        EnumSet.complementOf(allowedStates).forEach(state ->
                assertFalse("the owner of an assessment Interview should not be able to read that assessment Interview on the dashboard",
                        rules.userCanReadAssessmentInterviewOnDashboard(assessmentInterviews.get(state), assessorUser)));
    }

    @Test
    public void otherUsersCanNotReadAssessmentsOnDashboard() {
        EnumSet.allOf(AssessmentInterviewState.class).forEach(state ->
                assertFalse("other users should not be able to read any assessment Interviews",
                        rules.userCanReadAssessmentInterviewOnDashboard(assessmentInterviews.get(state), otherUser)));
    }

    private AssessmentInterviewResource setupAssessmentInterview(ProcessRole participant, AssessmentInterviewState state) {
        AssessmentInterview assessmentInterview = newAssessmentInterview()
                .withState(state)
                .build();

        when(assessmentInterviewRepositoryMock.findOne(assessmentInterview.getId())).thenReturn(assessmentInterview);

        return newAssessmentInterviewResource()
                .withId(assessmentInterview.getId())
                .withProcessRole(participant.getId())
                .build();
    }
}