package com.worth.ifs.form.security;


import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.application.builder.QuestionBuilder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.form.builder.FormInputBuilder;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.user.builder.UserResourceBuilder;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.resource.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static junit.framework.TestCase.assertFalse;
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
        processRoleForLeadOnApplicationOnOrganisation1 = newProcessRole().withApplication(application).withOrganisation(organisation1).build();
        formInputResponseUpdatedByLead = newFormInputResponseResource().withUpdatedBy(processRoleForLeadOnApplicationOnOrganisation1.getId()).withApplication(application.getId()).build();
        when(processRoleRepositoryMock.findOne(processRoleForLeadOnApplicationOnOrganisation1.getId())).thenReturn(processRoleForLeadOnApplicationOnOrganisation1);
        when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(leadApplicantForApplicationOnOrganisation1.getId(), getRole(LEADAPPLICANT).getId(), application.getId(), organisation1.getId())).thenReturn(newProcessRole().build());

        // Set up a collaborator who has answered a question
        collaboratorForApplicationOnOrganisation2 = UserResourceBuilder.newUserResource().build();
        processRoleForCollaboratorOnApplicationOnOrganisation2 = newProcessRole().withApplication(application).withOrganisation(organisation2).build();
        formInputResponseUpdatedByCollaborator = newFormInputResponseResource().withUpdatedBy(processRoleForCollaboratorOnApplicationOnOrganisation2.getId()).withApplication(application.getId()).build();
        when(processRoleRepositoryMock.findOne(processRoleForCollaboratorOnApplicationOnOrganisation2.getId())).thenReturn(processRoleForCollaboratorOnApplicationOnOrganisation2);
        when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(collaboratorForApplicationOnOrganisation2.getId(), getRole(COLLABORATOR).getId(), application.getId(), organisation2.getId())).thenReturn(newProcessRole().build());

        // Set up a question to which both lead applicant and collaborator should be able to see.
        final Question question = QuestionBuilder.newQuestion().withMultipleStatuses(false).build();
        final FormInput formInput = FormInputBuilder.newFormInput().withQuestion(question).build();
        sharedInputResponse = newFormInputResponseResource().withApplication(application.getId()).build();
        when(formInputRepositoryMock.findOne(sharedInputResponse.getFormInput())).thenReturn(formInput);
        when(processRoleRepositoryMock.findByUserIdAndApplicationId(leadApplicantForApplicationOnOrganisation1.getId(), application.getId())).thenReturn(newProcessRole().withRole(getRole(LEADAPPLICANT)).build());
        when(processRoleRepositoryMock.findByUserIdAndApplicationId(collaboratorForApplicationOnOrganisation2.getId(), application.getId())).thenReturn(newProcessRole().withRole(getRole(COLLABORATOR)).build());

        userNotOnApplication = UserResourceBuilder.newUserResource().build();
    }


    @Test
    public void testConsortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication() {
        assertTrue(rules.consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(formInputResponseUpdatedByLead, leadApplicantForApplicationOnOrganisation1));
        assertTrue(rules.consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(formInputResponseUpdatedByCollaborator, collaboratorForApplicationOnOrganisation2));
        assertFalse(rules.consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(formInputResponseUpdatedByLead, collaboratorForApplicationOnOrganisation2));
        assertFalse(rules.consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(formInputResponseUpdatedByCollaborator, leadApplicantForApplicationOnOrganisation1));
    }

    @Test
    public void testConsortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations() {
        assertTrue(rules.consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(sharedInputResponse, leadApplicantForApplicationOnOrganisation1));
        assertTrue(rules.consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(sharedInputResponse, collaboratorForApplicationOnOrganisation2));
        assertFalse(rules.consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(sharedInputResponse, userNotOnApplication));
    }

    @Test
    public void testCompAdminCanSeeFormInputResponsesForApplications() {
        assertTrue(rules.compAdminCanSeeFormInputResponsesForApplications(sharedInputResponse, compAdminUser()));
        assertFalse(rules.compAdminCanSeeFormInputResponsesForApplications(sharedInputResponse, leadApplicantForApplicationOnOrganisation1));
    }
    
    @Test
    public void testProjectFinanceUserCanSeeFormInputResponsesForApplications() {
        assertTrue(rules.projectFinanceUserCanSeeFormInputResponsesForApplications(sharedInputResponse, projectFinanceUser()));
        assertFalse(rules.projectFinanceUserCanSeeFormInputResponsesForApplications(sharedInputResponse, leadApplicantForApplicationOnOrganisation1));
    }

}

