package com.worth.ifs.assessment.security;

import java.util.List;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;

import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.security.AssessmentPermissionRules.isOwner;
import static com.worth.ifs.assessment.security.AssessmentPermissionRules.userCanReadAssessment;
import static com.worth.ifs.assessment.security.AssessmentPermissionRules.userCanUpdateAssessment;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.APPLICANT;
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssessmentPermissionRulesTest {
    private Assessment assessment;
    private UserResource applicant;
    private UserResource compadmin;
    private UserResource assessor;

    @Before
    public void setup(){
        Long assessorId = 123L;
        List<RoleResource> compadminRole = newRoleResource().withType(COMP_ADMIN).build(1);
        List<RoleResource> applicantRole = newRoleResource().withType(APPLICANT).build(1);
        List<RoleResource> assessorRole = newRoleResource().withType(ASSESSOR).build(1);
        Application application = newApplication().build();
        ProcessRole assessorProcessRole = newProcessRole().withApplication(application).withUser(newUser().withid(assessorId).build()).build();
        ProcessRole assessmentProcessRole = newProcessRole().withApplication(application).build();
        assessment = newAssessment().withProcessRole(assessmentProcessRole).build();
        applicant = newUserResource().withRolesGlobal(applicantRole).build();
        compadmin = newUserResource().withRolesGlobal(compadminRole).build();
        assessor = newUserResource().withId(assessorId).withRolesGlobal(assessorRole).build();
    }


    @Test
    public void compAdminCanReadAssessment(){
        assertTrue("a compadmin should be able to read an assessment", userCanReadAssessment(assessment, compadmin));
    }

    @Test
    public void ownerCanReadAssessment(){
        assertTrue("the owner of an assessment should be able to read that assessment", userCanReadAssessment(assessment, assessor));
    }

    @Test
    public void otherUsersCanNotReadAssessment(){
        assertFalse("other users should not be able to read any assessments", userCanReadAssessment(assessment, applicant));
    }

    @Test
    public void connectedUserShouldBeRecognised(){
        assertTrue("the owner of an assessment should be recognised as connected", isOwner(assessment, assessor));
    }

    @Test
    public void unconnectedUserShouldNotBeRecognised(){
        assertFalse("other users should not be recognised as connected", isOwner(assessment, applicant));
    }

    @Test
    public void ownersCanUpdateAssessments(){
        assertTrue("the owner of an assessment should able to update that assessment", userCanUpdateAssessment(assessment, assessor));
    }

    @Test
    public void compAdminsCanNotUpdateAssessments(){
        assertFalse("competition admins should not able to update assessments", userCanUpdateAssessment(assessment, compadmin));
    }

    @Test
    public void OtherUsersCanNotUpdateAssessments(){
        assertFalse("other users should not able to update assessments", userCanUpdateAssessment(assessment, applicant));
    }
}
