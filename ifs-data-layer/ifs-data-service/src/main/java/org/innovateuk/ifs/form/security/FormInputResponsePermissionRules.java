package org.innovateuk.ifs.form.security;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputResponseCommand;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.security.SecurityRuleUtil.checkProcessRole;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;

@PermissionRules
@Component
public class FormInputResponsePermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

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

    @PermissionRule(value = "READ", description = "An internal user can see form input responses for applications")
    public boolean internalUserCanSeeFormInputResponsesForApplications(final FormInputResponseResource response, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "SAVE",
            description = "A consortium member can update the response.")
    public boolean aConsortiumMemberCanUpdateAFormInputResponse(final FormInputResponseCommand response, final UserResource user) {
        final long applicationId = response.getApplicationId();
        final boolean isLead = checkProcessRole(user, applicationId, UserRoleType.LEADAPPLICANT, processRoleRepository);
        final boolean isCollaborator = checkProcessRole(user, applicationId, UserRoleType.COLLABORATOR, processRoleRepository);

        List<QuestionStatus> questionStatuses = getQuestionStatuses(response);

        // There is no question status yet, so only check for roles
        if (questionStatuses.isEmpty()) {
            return isLead || isCollaborator;
        }

        return (isLead || isCollaborator)
                && checkIfAssignedToQuestion(questionStatuses, user)
                && checkQuestionStatuses(questionStatuses, user);

    }

    private boolean checkQuestionStatuses(List<QuestionStatus> questionStatuses, UserResource user) {
        Question question = questionStatuses.get(0).getQuestion();
        if (question.getMultipleStatuses()) {
            return !checkIfAnyMarkedQuestionByUser(questionStatuses, user);
        } else {
            return !checkIfQuestionIsMarked(questionStatuses);
        }
    }

    private List<QuestionStatus> getQuestionStatuses(FormInputResponseCommand responseCommand) {
        FormInput formInput = formInputRepository.findOne(responseCommand.getFormInputId());
        return questionStatusRepository.findByQuestionIdAndApplicationId(formInput.getQuestion().getId(), responseCommand.getApplicationId());
    }


    private boolean checkIfAssignedToQuestion(List<QuestionStatus> questionStatuses, final UserResource user) {
        boolean isAssigned = questionStatuses.stream()
                .anyMatch(questionStatus -> questionStatus.getAssignee() == null
                        || questionStatus.getAssignee().getUser().getId().equals(user.getId()));

        return isAssigned;
    }

    private boolean checkIfQuestionIsMarked(List<QuestionStatus> questionStatuses) {
        boolean isMarked = questionStatuses.stream()
                .anyMatch(this::isMarkedAsComplete);

        return isMarked;
    }

    private boolean checkIfAnyMarkedQuestionByUser(List<QuestionStatus> questionStatuses, UserResource user) {
        return questionStatuses.stream()
                .anyMatch(questionStatus -> isMarkedAsComplete(questionStatus) && questionStatus.getMarkedAsCompleteBy().getUser().getId().equals(user.getId()));
    }

    private boolean isMarkedAsComplete(QuestionStatus questionStatus) {
        return questionStatus.getMarkedAsComplete() != null && questionStatus.getMarkedAsComplete().equals(true);
    }

    private boolean checkRoleForApplicationAndOrganisation(UserResource user, FormInputResponseResource response, UserRoleType userRoleType) {
        final Long organisationId = processRoleRepository.findOne(response.getUpdatedBy()).getOrganisationId();
        final Long applicationId = response.getApplication();
        return checkProcessRole(user, applicationId, organisationId, userRoleType, roleRepository, processRoleRepository);
    }
}

