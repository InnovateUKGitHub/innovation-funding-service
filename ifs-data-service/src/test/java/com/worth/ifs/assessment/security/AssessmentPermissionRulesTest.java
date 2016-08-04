package com.worth.ifs.assessment.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssessmentPermissionRulesTest extends BasePermissionRulesTest<AssessmentPermissionRules> {
    private Assessment assessment;
    private UserResource applicant;
    private UserResource compadmin;
    private UserResource assessor;


    @Override
    protected AssessmentPermissionRules supplyPermissionRulesUnderTest() {
            return new AssessmentPermissionRules();
    }

    @Before
    public void setup(){
        Long assessorId = 123L;
        List<RoleResource> compadminRole = newRoleResource().withType(COMP_ADMIN).build(1);
        List<RoleResource> applicantRole = newRoleResource().withType(APPLICANT).build(1);
        List<RoleResource> assessorRole = newRoleResource().withType(ASSESSOR).build(1);
        Application application = newApplication().build();
        ProcessRole assessorProcessRole = newProcessRole().withApplication(application).withUser(newUser().withid(assessorId).build()).build();
        assessment = newAssessment().withProcessRole(assessorProcessRole).build();
        applicant = newUserResource().withRolesGlobal(applicantRole).build();
        compadmin = newUserResource().withRolesGlobal(compadminRole).build();
        assessor = newUserResource().withId(assessorId).withRolesGlobal(assessorRole).build();
    }


    @Test
    public void compAdminCanReadAssessment(){
        assertTrue("a compadmin should be able to read an assessment", rules.userCanReadAssessment(assessment, compadmin));
    }

    @Test
    public void ownerCanReadAssessment(){
        assertTrue("the owner of an assessment should be able to read that assessment", rules.userCanReadAssessment(assessment, assessor));
    }

    @Test
    public void otherUsersCanNotReadAssessment(){
        assertFalse("other users should not be able to read any assessments", rules.userCanReadAssessment(assessment, applicant));
    }

    @Test
    public void connectedUserShouldBeRecognised(){
        assertTrue("the owner of an assessment should be recognised as connected", rules.isAssessor(assessment, assessor));
    }

    @Test
    public void unconnectedUserShouldNotBeRecognised(){
        assertFalse("other users should not be recognised as connected", rules.isAssessor(assessment, applicant));
    }

    @Test
    public void ownersCanUpdateAssessments(){
        assertTrue("the owner of an assessment should able to update that assessment", rules.userCanUpdateAssessment(assessment, assessor));
    }

    @Test
    public void compAdminsCanNotUpdateAssessments(){
        assertFalse("competition admins should not able to update assessments", rules.userCanUpdateAssessment(assessment, compadmin));
    }

    @Test
    public void OtherUsersCanNotUpdateAssessments(){
        assertFalse("other users should not able to update assessments", rules.userCanUpdateAssessment(assessment, applicant));
    }
}
