package com.worth.ifs.form.security;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.security.ApplicationPermissionRules;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.resource.FormInputResponseCommand;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.worth.ifs.security.SecurityRuleUtil.checkProcessRole;
import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;
import static com.worth.ifs.security.SecurityRuleUtil.isProjectFinanceUser;
import static com.worth.ifs.user.resource.UserRoleType.*;

@PermissionRules
@Component
public class FormInputResponsePermissionRules {
    private static final Log LOG = LogFactory.getLog(ApplicationPermissionRules.class);

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @PermissionRule(value = "READ", description = "The consortium can see the input responses of their organisation and application")
    public boolean consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(final FormInputResponseResource response, final UserResource user) {
        final boolean isLeadApplicantForOrganisation = checkRoleForApplicationAndOrganisation(user, response, LEADAPPLICANT);
        final boolean isCollaboratorForOrganisation = checkRoleForApplicationAndOrganisation(user, response, COLLABORATOR);
        return isLeadApplicantForOrganisation || isCollaboratorForOrganisation;
    }

    @PermissionRule(value = "READ", description = "The consortium can see the input responses of the application when the response is shared between organisations")
    public boolean consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(final FormInputResponseResource response, final UserResource user) {
        final FormInput formInput = formInputRepository.findOne(response.getFormInput());
        final Question question = formInput.getQuestion();
        if (!question.getMultipleStatuses()) {
            final boolean isLeadApplicant = checkProcessRole(user, response.getApplication(), LEADAPPLICANT, processRoleRepository);
            final boolean isCollaborator = checkProcessRole(user, response.getApplication(), COLLABORATOR, processRoleRepository);
            return isCollaborator || isLeadApplicant;
        }
        return false;
    }

    @PermissionRule(value = "READ", description = "The assessor can see the input responses of in applications for the applications they assess")
    public boolean assessorCanSeeTheInputResponsesInApplicationsForOrganisationsTheyAssess(final FormInputResponseResource response, final UserResource user) {
        final boolean isAssessor = checkProcessRole(user, response.getApplication(), ASSESSOR, processRoleRepository);
        return isAssessor;
    }

    @PermissionRule(value = "READ", description = "A comp admin can see form input responses for applications")
    public boolean compAdminCanSeeFormInputResponsesForApplications(final FormInputResponseResource response, final UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(value = "READ", description = "A project finance user can see form input responses for applications")
    public boolean projectFinanceUserCanSeeFormInputResponsesForApplications(final FormInputResponseResource response, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "SAVE",
            description = "A consortium member can update the response.")
    public boolean aConsortiumMemberCanUpdateAFormInputResponse(final FormInputResponseCommand response, final UserResource user) {
        final long applicationId = response.getApplicationId();
        final boolean isLead = checkProcessRole(user, applicationId, UserRoleType.LEADAPPLICANT, processRoleRepository);
        final boolean isCollaborator = checkProcessRole(user, applicationId, UserRoleType.COLLABORATOR, processRoleRepository);

        List<QuestionStatus> questionStatuses = getQuestionStatuses(response);


        // There is no question status yet, so only check for roles
        if(questionStatuses.isEmpty()) {
            return isLead || isCollaborator;
        }

        return (isLead || isCollaborator)
                && checkIfAssignedToQuestion(questionStatuses, user)
                && !checkIfQuestionIsMarked(questionStatuses);
    }

    private List<QuestionStatus> getQuestionStatuses(FormInputResponseCommand responseCommand) {
        FormInput formInput = formInputRepository.findOne(responseCommand.getFormInputId());
        List<QuestionStatus> questionStatuses = formInput.getQuestion().getQuestionStatuses();

        return questionStatuses.stream()
                .filter(questionStatus -> questionStatus.getApplication().getId().equals(responseCommand.getApplicationId())).collect(Collectors.toList());
    }


    private boolean checkIfAssignedToQuestion(List<QuestionStatus> questionStatuses, final UserResource user) {
        boolean isAssigned = questionStatuses.stream()
                .anyMatch(questionStatus -> questionStatus.getAssignee() == null
                                || questionStatus.getAssignee().getUser().getId().equals(user.getId()));

        return isAssigned;
    }

    private boolean checkIfQuestionIsMarked(List<QuestionStatus> questionStatuses) {
        boolean isMarked = questionStatuses.stream()
                .anyMatch(questionStatus -> questionStatus.getMarkedAsComplete() != null && questionStatus.getMarkedAsComplete().equals(true));

        return isMarked;
    }

    private boolean checkRoleForApplicationAndOrganisation(UserResource user, FormInputResponseResource response, UserRoleType userRoleType) {
        final Long organisationId = processRoleRepository.findOne(response.getUpdatedBy()).getOrganisation().getId();
        final Long applicationId = response.getApplication();
        return checkProcessRole(user, applicationId, organisationId, userRoleType, roleRepository, processRoleRepository);
    }
}

