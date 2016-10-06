package com.worth.ifs.assessment.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.mapper.AssessmentMapper;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AssessmentPermissionRulesTest extends BasePermissionRulesTest<AssessmentPermissionRules> {

    private UserResource assessorUser;
    private UserResource otherUser;
    private AssessmentResource assessmentResource;

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

        Assessment assessment = newAssessment().withParticipant(processRole).build();
        assessmentResource = newAssessmentResource()
                .withId(assessment.getId())
                .withProcessRole(processRole.getId())
                .build();

        when(processRoleRepositoryMock.findOne(processRole.getId())).thenReturn(processRole);
        when(assessmentRepositoryMock.findOne(assessment.getId())).thenReturn(assessment);
    }

    @Test
    public void ownerCanReadAssessment() {
        assertTrue("the owner of an assessment should be able to read that assessment", rules.userCanReadAssessment(assessmentResource, assessorUser));
    }

    @Test
    public void otherUsersCanNotReadAssessment() {
        assertFalse("other users should not be able to read any assessments", rules.userCanReadAssessment(assessmentResource, otherUser));
    }

    @Test
    public void ownersCanUpdateAssessments() {
        assertTrue("the owner of an assessment should able to update that assessment", rules.userCanUpdateAssessment(assessmentResource, assessorUser));
    }

    @Test
    public void otherUsersCanNotUpdateAssessments() {
        assertFalse("other users should not able to update assessments", rules.userCanUpdateAssessment(assessmentResource, otherUser));
    }
}
