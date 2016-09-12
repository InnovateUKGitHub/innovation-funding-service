package com.worth.ifs.assessment.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
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
    private AssessmentResource assessment;

    @Override
    protected AssessmentPermissionRules supplyPermissionRulesUnderTest() {
        return new AssessmentPermissionRules();
    }

    @Before
    public void setup() {
        Long processRoleId = 1L;
        Long userId = 2L;

        assessorUser = newUserResource().withId(userId).build();
        otherUser = newUserResource().build();

        ProcessRole processRole = newProcessRole()
                .withUser(newUser().with(id(userId)).build())
                .build();

        assessment = newAssessmentResource().withProcessRole(processRole.getId()).build();

        when(processRoleRepositoryMock.findOne(processRoleId)).thenReturn(processRole);
    }

    @Test
    public void ownerCanReadAssessment() {
        assertTrue("the owner of an assessment should be able to read that assessment", rules.userCanReadAssessment(assessment, assessorUser));
    }

    @Test
    public void otherUsersCanNotReadAssessment() {
        assertFalse("other users should not be able to read any assessments", rules.userCanReadAssessment(assessment, otherUser));
    }

    @Test
    public void ownersCanUpdateAssessments() {
        assertTrue("the owner of an assessment should able to update that assessment", rules.userCanUpdateAssessment(assessment, assessorUser));
    }

    @Test
    public void otherUsersCanNotUpdateAssessments() {
        assertFalse("other users should not able to update assessments", rules.userCanUpdateAssessment(assessment, otherUser));
    }
}
