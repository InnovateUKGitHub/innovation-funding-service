package org.innovateuk.ifs.application.security;


import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.builder.FormInputBuilder;
import org.innovateuk.ifs.form.builder.QuestionBuilder;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class FormInputResponsePermissionRulesTest extends BasePermissionRulesTest<FormInputResponsePermissionRules> {

    private FormInputResponseResource sharedInputResponse;
    private FormInputResponseResource formInputResponseUpdatedByLead;
    private FormInputResponseResource formInputResponseUpdatedByCollaborator;
    private UserResource leadApplicantForApplicationOnOrganisation1;
    private ProcessRole processRoleForLeadOnApplicationOnOrganisation1;
    private ProcessRole processRoleForCollaboratorOnApplicationOnOrganisation2;
    private UserResource collaboratorForApplicationOnOrganisation2;
    private Organisation organisation1;
    private Organisation organisation2;
    private Application application;
    private UserResource userNotOnApplication;
    private UserResource assessorForApplication;
    private UserResource panelAssessorForApplication;


    @Override
    protected FormInputResponsePermissionRules supplyPermissionRulesUnderTest() {
        return new FormInputResponsePermissionRules();
    }

    @Before
    public void setup() throws Exception {

        application = newApplication().build();
        organisation1 = newOrganisation().build();
        organisation2 = newOrganisation().build();

        // Set up a lead applicant who has answered a question.
        leadApplicantForApplicationOnOrganisation1 = UserResourceBuilder.newUserResource().build();
        processRoleForLeadOnApplicationOnOrganisation1 = newProcessRole().withApplication(application).withOrganisationId(organisation1.getId()).build();
        formInputResponseUpdatedByLead = newFormInputResponseResource().withUpdatedBy(processRoleForLeadOnApplicationOnOrganisation1.getId()).withApplication(application.getId()).build();
        when(processRoleRepositoryMock.findOne(processRoleForLeadOnApplicationOnOrganisation1.getId())).thenReturn(processRoleForLeadOnApplicationOnOrganisation1);
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationIdAndOrganisationId(leadApplicantForApplicationOnOrganisation1.getId(),  Role.LEADAPPLICANT, application.getId(), organisation1.getId())).thenReturn(newProcessRole().build());

        // Set up a collaborator who has answered a question
        collaboratorForApplicationOnOrganisation2 = UserResourceBuilder.newUserResource().build();
        processRoleForCollaboratorOnApplicationOnOrganisation2 = newProcessRole().withApplication(application).withOrganisationId(organisation2.getId()).build();
        formInputResponseUpdatedByCollaborator = newFormInputResponseResource().withUpdatedBy(processRoleForCollaboratorOnApplicationOnOrganisation2.getId()).withApplication(application.getId()).build();
        when(processRoleRepositoryMock.findOne(processRoleForCollaboratorOnApplicationOnOrganisation2.getId())).thenReturn(processRoleForCollaboratorOnApplicationOnOrganisation2);
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationIdAndOrganisationId(collaboratorForApplicationOnOrganisation2.getId(),  Role.COLLABORATOR, application.getId(), organisation2.getId())).thenReturn(newProcessRole().build());

        // Set up a question to which both lead applicant and collaborator should be able to see.
        final Question question = QuestionBuilder.newQuestion().withMultipleStatuses(false).build();
        final FormInput formInput = FormInputBuilder.newFormInput().withQuestion(question).build();
        sharedInputResponse = newFormInputResponseResource().withApplication(application.getId()).build();
        when(formInputRepositoryMock.findOne(sharedInputResponse.getFormInput())).thenReturn(formInput);
        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(leadApplicantForApplicationOnOrganisation1.getId(), application.getId(), Role.LEADAPPLICANT)).thenReturn(true);
        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(collaboratorForApplicationOnOrganisation2.getId(), application.getId(), Role.COLLABORATOR)).thenReturn(true);

        userNotOnApplication = UserResourceBuilder.newUserResource().build();

       assessorForApplication = UserResourceBuilder.newUserResource().build();
        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(assessorForApplication.getId(), application.getId(), Role.ASSESSOR)).thenReturn(true);
        panelAssessorForApplication = UserResourceBuilder.newUserResource().build();
        when(processRoleRepositoryMock.existsByUserIdAndApplicationIdAndRole(panelAssessorForApplication.getId(), application.getId(), Role.PANEL_ASSESSOR)).thenReturn(true);


    }


    @Test
    public void consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication() {
        assertTrue(rules.consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(formInputResponseUpdatedByLead, leadApplicantForApplicationOnOrganisation1));
        assertTrue(rules.consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(formInputResponseUpdatedByCollaborator, collaboratorForApplicationOnOrganisation2));
        assertFalse(rules.consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(formInputResponseUpdatedByLead, collaboratorForApplicationOnOrganisation2));
        assertFalse(rules.consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(formInputResponseUpdatedByCollaborator, leadApplicantForApplicationOnOrganisation1));
    }

    @Test
    public void consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations() {
        assertTrue(rules.consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(sharedInputResponse, leadApplicantForApplicationOnOrganisation1));
        assertTrue(rules.consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(sharedInputResponse, collaboratorForApplicationOnOrganisation2));
        assertFalse(rules.consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(sharedInputResponse, userNotOnApplication));
    }

    @Test
    public void internalUserCanSeeFormInputResponsesForApplications() {
        assertTrue(rules.internalUserCanSeeFormInputResponsesForApplications(sharedInputResponse, compAdminUser()));
        assertTrue(rules.internalUserCanSeeFormInputResponsesForApplications(sharedInputResponse, projectFinanceUser()));
        assertFalse(rules.internalUserCanSeeFormInputResponsesForApplications(sharedInputResponse, leadApplicantForApplicationOnOrganisation1));
    }

    @Test
    public void assessorCanSeeTheInputResponsesInApplicationsForOrganisationsTheyAssess() {
        assertTrue(rules.assessorCanSeeTheInputResponsesInApplicationsTheyAssess(formInputResponseUpdatedByLead, assessorForApplication));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyAssess(formInputResponseUpdatedByLead, panelAssessorForApplication));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyAssess(formInputResponseUpdatedByLead, leadApplicantForApplicationOnOrganisation1));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyAssess(formInputResponseUpdatedByLead, collaboratorForApplicationOnOrganisation2));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyAssess(formInputResponseUpdatedByLead, userNotOnApplication));
    }

    @Test
    public void assessorCanSeeTheInputResponsesInApplicationsForOrganisationsTheyReview() {
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyReview(formInputResponseUpdatedByLead, assessorForApplication));
        assertTrue(rules.assessorCanSeeTheInputResponsesInApplicationsTheyReview(formInputResponseUpdatedByLead, panelAssessorForApplication));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyReview(formInputResponseUpdatedByLead, leadApplicantForApplicationOnOrganisation1));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyReview(formInputResponseUpdatedByLead, collaboratorForApplicationOnOrganisation2));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyReview(formInputResponseUpdatedByLead, userNotOnApplication));
    }
}