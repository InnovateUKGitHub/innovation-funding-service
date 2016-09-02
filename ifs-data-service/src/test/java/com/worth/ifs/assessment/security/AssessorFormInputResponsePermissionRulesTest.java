package com.worth.ifs.assessment.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AssessorFormInputResponsePermissionRulesTest extends BasePermissionRulesTest<AssessorFormInputResponsePermissionRules> {

    private UserResource applicantUser;
    private UserResource assessorUser;
    private AssessorFormInputResponseResource response;

    @Before
    public void setUp() throws Exception {
        Long processRoleId = 1L;
        Long userId = 2L;

        applicantUser = newUserResource().build();
        assessorUser = newUserResource().withId(userId).build();

        ProcessRole processRole = newProcessRole()
                .withUser(newUser().with(id(userId)).build())
                .withRole(ASSESSOR)
                .build();

        Assessment assessment = newAssessment().withProcessRole(processRole).build();
        response = newAssessorFormInputResponseResource().withAssessment(assessment.getId()).build();

        when(processRoleRepositoryMock.findOne(processRoleId)).thenReturn(processRole);
        when(assessmentRepositoryMock.findOneByProcessRoleId(response.getAssessment())).thenReturn(assessment);
    }

    @Override
    protected AssessorFormInputResponsePermissionRules supplyPermissionRulesUnderTest() {
        return new AssessorFormInputResponsePermissionRules();
    }

    @Test
    public void compAdminCanReadAssessorFormInputResponse() {
        assertTrue("a compadmin should be able to read a Response", rules.userCanReadAssessorFormInputResponse(response, compAdminUser()));
    }

    @Test
    public void ownerCanReadAssessorFormInputResponse() {
        assertTrue("the owner of a Response should be able to read that Response", rules.userCanReadAssessorFormInputResponse(response, assessorUser));
    }

    @Test
    public void otherUsersCanNotReadAssessorFormInputResponse() {
        assertFalse("other users should not be able to read any Responses", rules.userCanReadAssessorFormInputResponse(response, applicantUser));
    }

    @Test
    public void ownersCanUpdateAssessorFormInputResponses() {
        assertTrue("the owner of a Response should able to update that Response", rules.userCanUpdateAssessorFormInputResponse(response, assessorUser));
    }

    @Test
    public void compAdminsCanNotUpdateAssessorFormInputResponse() {
        assertFalse("competition admins should not able to update Responses", rules.userCanUpdateAssessorFormInputResponse(response, compAdminUser()));
    }

    @Test
    public void OtherUsersCanNotUpdateAssessorFormInputResponse() {
        assertFalse("other users should not able to update Responses", rules.userCanUpdateAssessorFormInputResponse(response, applicantUser));
    }
}