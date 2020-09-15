package org.innovateuk.ifs.application.security;


import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.builder.FormInputBuilder;
import org.innovateuk.ifs.form.builder.QuestionBuilder;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class FormInputResponsePermissionRulesTest extends BasePermissionRulesTest<FormInputResponsePermissionRules> {

    private FormInputResponseResource sharedInputResponse;
    private FormInputResponseResource formInputResponseUpdatedByLead;
    private FormInputResponseResource formInputResponseUpdatedByCollaborator;
    private UserResource leadApplicantForApplicationOnOrganisation1;
    private UserResource collaboratorForApplicationOnOrganisation2;
    private Application application;
    private UserResource userNotOnApplication;
    private UserResource assessorForApplication;
    private UserResource panelAssessorForApplication;
    private UserResource interviewAssessorForApplication;

    @Mock
    private FormInputRepository formInputRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Override
    protected FormInputResponsePermissionRules supplyPermissionRulesUnderTest() {
        return new FormInputResponsePermissionRules();
    }

    @Before
    public void setup() {

        application = newApplication().build();
        Organisation organisation1 = newOrganisation().build();
        Organisation organisation2 = newOrganisation().build();

        // Set up a lead applicant who has answered a question.
        leadApplicantForApplicationOnOrganisation1 = newUserResource().build();
        ProcessRole processRoleForLeadOnApplicationOnOrganisation1 = newProcessRole().withApplication(application).withOrganisationId(organisation1.getId()).build();
        formInputResponseUpdatedByLead = newFormInputResponseResource().withUpdatedBy(processRoleForLeadOnApplicationOnOrganisation1.getId()).withApplication(application.getId()).build();
        when(processRoleRepository.findById(processRoleForLeadOnApplicationOnOrganisation1.getId())).thenReturn(Optional.of(processRoleForLeadOnApplicationOnOrganisation1));
        when(processRoleRepository.findByUserIdAndRoleAndApplicationIdAndOrganisationId(leadApplicantForApplicationOnOrganisation1.getId(), Role.LEADAPPLICANT, application.getId(), organisation1.getId())).thenReturn(newProcessRole().build());

        // Set up a collaborator who has answered a question
        collaboratorForApplicationOnOrganisation2 = newUserResource().build();
        ProcessRole processRoleForCollaboratorOnApplicationOnOrganisation2 = newProcessRole().withApplication(application).withOrganisationId(organisation2.getId()).build();
        formInputResponseUpdatedByCollaborator = newFormInputResponseResource().withUpdatedBy(processRoleForCollaboratorOnApplicationOnOrganisation2.getId()).withApplication(application.getId()).build();
        when(processRoleRepository.findById(processRoleForCollaboratorOnApplicationOnOrganisation2.getId())).thenReturn(Optional.of(processRoleForCollaboratorOnApplicationOnOrganisation2));
        when(processRoleRepository.findByUserIdAndRoleAndApplicationIdAndOrganisationId(collaboratorForApplicationOnOrganisation2.getId(), Role.COLLABORATOR, application.getId(), organisation2.getId())).thenReturn(newProcessRole().build());

        // Set up a question to which both lead applicant and collaborator should be able to see.
        final Question question = QuestionBuilder.newQuestion().withMultipleStatuses(false).build();
        final FormInput formInput = FormInputBuilder.newFormInput().withQuestion(question).build();
        sharedInputResponse = newFormInputResponseResource().withApplication(application.getId()).build();
        when(formInputRepository.findById(sharedInputResponse.getFormInput())).thenReturn(Optional.of(formInput));
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(leadApplicantForApplicationOnOrganisation1.getId(), application.getId(), Role.LEADAPPLICANT)).thenReturn(true);
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(collaboratorForApplicationOnOrganisation2.getId(), application.getId(), Role.COLLABORATOR)).thenReturn(true);

        userNotOnApplication = newUserResource().build();

        assessorForApplication = newUserResource().build();
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(assessorForApplication.getId(), application.getId(), Role.ASSESSOR)).thenReturn(true);
        panelAssessorForApplication = newUserResource().build();
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(panelAssessorForApplication.getId(), application.getId(), Role.PANEL_ASSESSOR)).thenReturn(true);
        interviewAssessorForApplication = newUserResource().build();
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(interviewAssessorForApplication.getId(), application.getId(), Role.INTERVIEW_ASSESSOR)).thenReturn(true);

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
    public void stakeholdersCanSeeFormInputResponsesForApplications() {
        Competition competition = newCompetition().build();
        application.setCompetition(competition);
        UserResource stakeholderUserResource = newUserResource()
                .withRoleGlobal(STAKEHOLDER)
                .build();

        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeholderUserResource.getId())).thenReturn(true);

        assertTrue(rules.stakeholdersCanSeeFormInputResponsesForApplications(sharedInputResponse, stakeholderUserResource));
        allInternalUsers.forEach(user -> Assert.assertFalse(rules.stakeholdersCanSeeFormInputResponsesForApplications(sharedInputResponse, user)));
    }

    @Test
    public void competitionFinanceUsersCanSeeFormInputResponsesForApplications() {
        Competition competition = newCompetition().build();
        application.setCompetition(competition);
        UserResource competitionFinanceUserResource = newUserResource()
                .withRoleGlobal(EXTERNAL_FINANCE)
                .build();

        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), competitionFinanceUserResource.getId())).thenReturn(true);

        assertTrue(rules.competitionFinanceUsersCanSeeFormInputResponsesForApplications(sharedInputResponse, competitionFinanceUserResource));
        allInternalUsers.forEach(user -> Assert.assertFalse(rules.competitionFinanceUsersCanSeeFormInputResponsesForApplications(sharedInputResponse, user)));
    }

    @Test
    public void assessorCanSeeTheInputResponsesInApplicationsForOrganisationsTheyAssess() {
        assertTrue(rules.assessorCanSeeTheInputResponsesInApplicationsTheyAssess(formInputResponseUpdatedByLead, assessorForApplication));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyAssess(formInputResponseUpdatedByLead, panelAssessorForApplication));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyAssess(formInputResponseUpdatedByLead, leadApplicantForApplicationOnOrganisation1));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyAssess(formInputResponseUpdatedByLead, collaboratorForApplicationOnOrganisation2));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyAssess(formInputResponseUpdatedByLead, userNotOnApplication));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyAssess(formInputResponseUpdatedByLead, interviewAssessorForApplication));
    }

    @Test
    public void assessorCanSeeTheInputResponsesInApplicationsForOrganisationsTheyReview() {
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyReview(formInputResponseUpdatedByLead, assessorForApplication));
        assertTrue(rules.assessorCanSeeTheInputResponsesInApplicationsTheyReview(formInputResponseUpdatedByLead, panelAssessorForApplication));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyReview(formInputResponseUpdatedByLead, leadApplicantForApplicationOnOrganisation1));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyReview(formInputResponseUpdatedByLead, collaboratorForApplicationOnOrganisation2));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyReview(formInputResponseUpdatedByLead, userNotOnApplication));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyReview(formInputResponseUpdatedByLead, interviewAssessorForApplication));
    }

    @Test
    public void monitoringOfficerCanSeeApplicationFinanceTotals() {
        Project project = newProject().build();
        when(projectRepository.findOneByApplicationId(anyLong())).thenReturn(project);
        when(projectMonitoringOfficerRepository.existsByProjectIdAndUserId(project.getId(), monitoringOfficerUser().getId())).thenReturn(true);

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasRole(MONITORING_OFFICER)) {
                assertTrue(rules.monitoringOfficersCanSeeTheInputResponsesInApplicationsAssignedToThem(formInputResponseUpdatedByLead, monitoringOfficerUser()));
            } else {
                Assert.assertFalse(rules.monitoringOfficersCanSeeTheInputResponsesInApplicationsAssignedToThem(formInputResponseUpdatedByLead, user));
            }
        });
    }

    @Test
    public void assessorCanSeeTheInputResponsesInApplicationsForOrganisationsTheyInterview() {
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyInterview(formInputResponseUpdatedByLead, assessorForApplication));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyInterview(formInputResponseUpdatedByLead, panelAssessorForApplication));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyInterview(formInputResponseUpdatedByLead, leadApplicantForApplicationOnOrganisation1));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyInterview(formInputResponseUpdatedByLead, collaboratorForApplicationOnOrganisation2));
        assertFalse(rules.assessorCanSeeTheInputResponsesInApplicationsTheyInterview(formInputResponseUpdatedByLead, userNotOnApplication));
        assertTrue(rules.assessorCanSeeTheInputResponsesInApplicationsTheyInterview(formInputResponseUpdatedByLead, interviewAssessorForApplication));
    }
}