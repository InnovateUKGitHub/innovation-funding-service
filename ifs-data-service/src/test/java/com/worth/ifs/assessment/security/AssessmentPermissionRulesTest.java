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
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AssessmentPermissionRulesTest extends BasePermissionRulesTest<AssessmentPermissionRules> {

    private UserResource applicantUser;
    private UserResource assessorUser;
    private AssessmentResource assessment;

    @Override
    protected AssessmentPermissionRules supplyPermissionRulesUnderTest() {
        return new AssessmentPermissionRules();
    }

    @Before
    public void setup() {
        Long processRoleId = 1L;
        Long userId = 2L;

        applicantUser = newUserResource().build();
        assessorUser = newUserResource().withId(userId).build();

        ProcessRole processRole = newProcessRole()
                .withUser(newUser().with(id(userId)).build())
                .withRole(ASSESSOR)
                .build();

        assessment = newAssessmentResource().withProcessRole(processRole.getId()).build();

        when(processRoleRepositoryMock.findOne(processRoleId)).thenReturn(processRole);
    }

    @Test
    public void compAdminCanReadAssessment() {
        assertTrue("a compadmin should be able to read an assessment", rules.userCanReadAssessment(assessment, compAdminUser()));
    }

    @Test
    public void ownerCanReadAssessment() {
        assertTrue("the owner of an assessment should be able to read that assessment", rules.userCanReadAssessment(assessment, assessorUser));
    }

    @Test
    public void otherUsersCanNotReadAssessment() {
        assertFalse("other users should not be able to read any assessments", rules.userCanReadAssessment(assessment, applicantUser));
    }

    @Test
    public void ownersCanUpdateAssessments() {
        assertTrue("the owner of an assessment should able to update that assessment", rules.userCanUpdateAssessment(assessment, assessorUser));
    }

    @Test
    public void compAdminsCanNotUpdateAssessments() {
        assertFalse("competition admins should not able to update assessments", rules.userCanUpdateAssessment(assessment, compAdminUser()));
    }

    @Test
    public void OtherUsersCanNotUpdateAssessments() {
        assertFalse("other users should not able to update assessments", rules.userCanUpdateAssessment(assessment, applicantUser));
    }
}
