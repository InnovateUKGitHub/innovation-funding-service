package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponsesResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AssessorFormInputResponsePermissionRulesTest extends BasePermissionRulesTest<AssessorFormInputResponsePermissionRules> {

    private Assessment assessment;
    private UserResource applicantUser;
    private UserResource assessorUser;

    @Before
    public void setUp() throws Exception {

        applicantUser = newUserResource().build();
        assessorUser = newUserResource().build();

        ProcessRole processRole = newProcessRole()
                .withUser(newUser().with(id(assessorUser.getId())).build())
                .withRole(ASSESSOR)
                .build();

        assessment = newAssessment().withParticipant(processRole).build();

        when(processRoleRepositoryMock.findOne(processRole.getId())).thenReturn(processRole);
        when(assessmentRepositoryMock.findOne(assessment.getId())).thenReturn(assessment);
    }

    @Override
    protected AssessorFormInputResponsePermissionRules supplyPermissionRulesUnderTest() {
        return new AssessorFormInputResponsePermissionRules();
    }

    @Test
    public void ownersCanUpdateAssessorFormInputResponses() {
        AssessorFormInputResponsesResource responses = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .withAssessment(assessment.getId())
                        .build(2));

        assertTrue("the owner of all responses should be able to update those responses",
                rules.userCanUpdateAssessorFormInputResponses(responses, assessorUser));
    }

    @Test
    public void otherUsersCannotUpdateAssessorFormInputResponses() {
        AssessorFormInputResponsesResource responses = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .withAssessment(assessment.getId())
                        .build(2));

        assertFalse("other users should not be able to update responses",
                rules.userCanUpdateAssessorFormInputResponses(responses, applicantUser));
    }
}
