package org.innovateuk.ifs.interview.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.mapper.InterviewMapper;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.interview.resource.InterviewResource;
import org.innovateuk.ifs.interview.resource.InterviewState;
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
import static org.innovateuk.ifs.interview.builder.InterviewBuilder.newInterview;
import static org.innovateuk.ifs.interview.builder.InterviewResourceBuilder.newInterviewResource;
import static org.innovateuk.ifs.interview.resource.InterviewState.ASSIGNED;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class InterviewPermissionRulesTest extends BasePermissionRulesTest<InterviewPermissionRules> {

    private UserResource assessorUser;
    private UserResource otherUser;
    private Map<InterviewState, InterviewResource> assessmentInterviews;

    @Mock
    private InterviewRepository interviewRepositoryMock;

    @Autowired
    public InterviewMapper interviewMapper;

    @Override
    protected InterviewPermissionRules supplyPermissionRulesUnderTest() {
        return new InterviewPermissionRules();
    }

    @Before
    public void setup() {
        assessorUser = newUserResource().build();
        otherUser = newUserResource().build();

        ProcessRole processRole = newProcessRole()
                .withUser(newUser().with(id(assessorUser.getId())).build())
                .build();
        when(processRoleRepositoryMock.findById(processRole.getId())).thenReturn(Optional.of(processRole));

        assessmentInterviews = EnumSet.allOf(InterviewState.class).stream().collect(toMap(identity(), state -> setupAssessmentInterview(processRole, state)));
    }

    @Test
    public void ownersCanReadAssessmentsOnDashboard() {
        EnumSet<InterviewState> allowedStates = EnumSet.of(ASSIGNED);

        allowedStates.forEach(state ->
                assertTrue("the owner of an assessment Interview should be able to read that assessment Interview on the dashboard",
                        rules.userCanReadInterviewOnDashboard(assessmentInterviews.get(state), assessorUser)));

        EnumSet.complementOf(allowedStates).forEach(state ->
                assertFalse("the owner of an assessment Interview should not be able to read that assessment Interview on the dashboard",
                        rules.userCanReadInterviewOnDashboard(assessmentInterviews.get(state), assessorUser)));
    }

    @Test
    public void otherUsersCanNotReadAssessmentsOnDashboard() {
        EnumSet.allOf(InterviewState.class).forEach(state ->
                assertFalse("other users should not be able to read any assessment Interviews",
                        rules.userCanReadInterviewOnDashboard(assessmentInterviews.get(state), otherUser)));
    }

    private InterviewResource setupAssessmentInterview(ProcessRole participant, InterviewState state) {
        Interview interview = newInterview()
                .withState(state)
                .build();

        when(interviewRepositoryMock.findById(interview.getId())).thenReturn(Optional.of(interview));

        return newInterviewResource()
                .withId(interview.getId())
                .withProcessRole(participant.getId())
                .build();
    }
}