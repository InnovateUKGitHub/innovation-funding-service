package org.innovateuk.ifs.application.security;


import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.builder.FormInputBuilder;
import org.innovateuk.ifs.form.builder.QuestionBuilder;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
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
        when(processRoleRepository.existsByUserIdAndRoleAndApplicationIdAndOrganisationId(leadApplicantForApplicationOnOrganisation1.getId(), Role.LEADAPPLICANT, application.getId(), organisation1.getId())).thenReturn(true);

        // Set up a collaborator who has answered a question
        collaboratorForApplicationOnOrganisation2 = newUserResource().build();
        ProcessRole processRoleForCollaboratorOnApplicationOnOrganisation2 = newProcessRole().withApplication(application).withOrganisationId(organisation2.getId()).build();
        formInputResponseUpdatedByCollaborator = newFormInputResponseResource().withUpdatedBy(processRoleForCollaboratorOnApplicationOnOrganisation2.getId()).withApplication(application.getId()).build();
        when(processRoleRepository.findById(processRoleForCollaboratorOnApplicationOnOrganisation2.getId())).thenReturn(Optional.of(processRoleForCollaboratorOnApplicationOnOrganisation2));
        when(processRoleRepository.existsByUserIdAndRoleAndApplicationIdAndOrganisationId(collaboratorForApplicationOnOrganisation2.getId(), Role.COLLABORATOR, application.getId(), organisation2.getId())).thenReturn(true);

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
}