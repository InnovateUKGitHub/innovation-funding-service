package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AssessorFormInputResponsePermissionRulesTest extends BasePermissionRulesTest<AssessorFormInputResponsePermissionRules> {

    private UserResource applicantUser;
    private UserResource assessorUser;
    private AssessorFormInputResponseResource response;

    @Before
    public void setUp() throws Exception {

        applicantUser = newUserResource().build();
        assessorUser = newUserResource().build();

        ProcessRole processRole = newProcessRole()
                .withUser(newUser().with(id(assessorUser.getId())).build())
                .withRole(ASSESSOR)
                .build();

        Assessment assessment = newAssessment().withParticipant(processRole).build();
        response = newAssessorFormInputResponseResource().withAssessment(assessment.getId()).build();

        when(processRoleRepositoryMock.findOne(processRole.getId())).thenReturn(processRole);
        when(assessmentRepositoryMock.findOne(response.getAssessment())).thenReturn(assessment);
    }

    @Override
    protected AssessorFormInputResponsePermissionRules supplyPermissionRulesUnderTest() {
        return new AssessorFormInputResponsePermissionRules();
    }

    @Test
    public void ownersCanUpdateAssessorFormInputResponses() {
        assertTrue("the owner of a Response should able to update that Response", rules.userCanUpdateAssessorFormInputResponse(response, assessorUser));
    }

    @Test
    public void otherUsersCanNotUpdateAssessorFormInputResponse() {
        assertFalse("other users should not able to update Responses", rules.userCanUpdateAssessorFormInputResponse(response, applicantUser));
    }
}
