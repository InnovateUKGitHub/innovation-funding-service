package com.worth.ifs.assessment.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.mapper.AssessmentMapper;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.workflow.domain.ActivityState;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AssessmentPermissionRulesTest extends BasePermissionRulesTest<AssessmentPermissionRules> {

    private UserResource assessorUser;
    private UserResource otherUser;
    private AssessmentResource openAssessmentResource;
    private AssessmentResource rejectedAssessmentResource;
    private AssessmentResource submittedAssessmentResource;

    @Autowired
    public AssessmentMapper assessmentMapper;

    @Override
    protected AssessmentPermissionRules supplyPermissionRulesUnderTest() {
        return new AssessmentPermissionRules();
    }

    @Before
    public void setup() {

        assessorUser = newUserResource().build();
        otherUser = newUserResource().build();

        ProcessRole processRole = newProcessRole()
                .withUser(newUser().with(id(assessorUser.getId())).build())
                .build();

        Assessment openAssessment = newAssessment()
                .withParticipant(processRole)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, AssessmentStates.OPEN.getBackingState()))
                .build();
        Assessment rejectedAssessment = newAssessment()
                .withParticipant(processRole)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, AssessmentStates.REJECTED.getBackingState()))
                .build();
        Assessment submittedAssessment = newAssessment()
                .withParticipant(processRole)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, AssessmentStates.SUBMITTED.getBackingState()))
                .build();

        openAssessmentResource = newAssessmentResource()
                .withId(openAssessment.getId())
                .withProcessRole(processRole.getId())
                .build();

        rejectedAssessmentResource = newAssessmentResource()
                .withId(rejectedAssessment.getId())
                .withProcessRole(processRole.getId())
                .build();

        submittedAssessmentResource = newAssessmentResource()
                .withId(submittedAssessment.getId())
                .withProcessRole(processRole.getId())
                .build();

        when(processRoleRepositoryMock.findOne(processRole.getId())).thenReturn(processRole);
        when(assessmentRepositoryMock.findOne(openAssessment.getId())).thenReturn(openAssessment);
        when(assessmentRepositoryMock.findOne(rejectedAssessment.getId())).thenReturn(rejectedAssessment);
        when(assessmentRepositoryMock.findOne(submittedAssessment.getId())).thenReturn(submittedAssessment);
    }

    @Test
    public void ownerCanReadOpenAssessment() {
        assertTrue("the owner of an assessment should be able to read that assessment", rules.userCanReadAssessment(openAssessmentResource, assessorUser));
    }

    @Test
    public void ownerCanReadSubmittedAssessmentOnDashboard() {
        assertTrue("the owner of an assessment should be able to read that assessment", rules.userCanReadAssessment(submittedAssessmentResource, assessorUser));
    }

    @Test
    public void ownerCanReadOpenAssessmentNonDashboard() {
        assertTrue("the owner of an assessment should be able to read that assessment", rules.userCanReadAssessmentNonDashboard(openAssessmentResource, assessorUser));
    }

    @Test
    public void ownerCanNotReadSubmittedAssessmentNonDashboard() {
        assertFalse("the owner of an assessment should not be able to read that assessment", rules.userCanReadAssessmentNonDashboard(submittedAssessmentResource, assessorUser));
    }

    @Test
    public void otherUsersCanNotReadOpenAssessment() {
        assertFalse("other users should not be able to read any assessments", rules.userCanReadAssessment(openAssessmentResource, otherUser));
    }

    @Test
    public void otherUsersCanNotReadOpenAssessmentNonDashboard() {
        assertFalse("other users should not be able to read any assessments", rules.userCanReadAssessmentNonDashboard(openAssessmentResource, otherUser));
    }

    @Test
    public void ownersCanUpdateAssessments() {
        assertTrue("the owner of an assessment should able to update that assessment", rules.userCanUpdateAssessment(openAssessmentResource, assessorUser));
    }

    @Test
    public void ownersCanNotUpdateSubmittedAssessments() {
        assertFalse("the owner of an assessment should not be able to update that assessment", rules.userCanUpdateAssessment(submittedAssessmentResource, assessorUser));
    }

    @Test
    public void otherUsersCanNotUpdateAssessments() {
        assertFalse("other users should not able to update assessments", rules.userCanUpdateAssessment(openAssessmentResource, otherUser));
    }

    @Test
    public void ownersCanNotReadRejectedAssessments() {
        assertFalse("the owner of a rejected assessment should not be able to read that assessment", rules.userCanReadAssessment(rejectedAssessmentResource, assessorUser));
    }

    @Test
    public void ownersCanNotReadRejectedAssessmentsNonDashboard() {
        assertFalse("the owner of a rejected assessment should not be able to read that assessment", rules.userCanReadAssessmentNonDashboard(rejectedAssessmentResource, assessorUser));
    }

    @Test
    public void ownersUsersCanNotUpdateRejectedAssessments() {
        assertFalse("the owner of a rejected assessment should not be able to update that assessment", rules.userCanUpdateAssessment(rejectedAssessmentResource, assessorUser));
    }
}
