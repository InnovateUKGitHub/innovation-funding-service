package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.security.SecurityRuleUtil.checkProcessRole;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

@PermissionRules
@Component
public class FormInputResponsePermissionRules extends BasePermissionRules {

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
        final FormInput formInput = formInputRepository.findById(response.getFormInput()).get();
        final Question question = formInput.getQuestion();
        if (!question.getMultipleStatuses()) {
            final boolean isLeadApplicant = checkProcessRole(user, response.getApplication(), LEADAPPLICANT, processRoleRepository);
            final boolean isCollaborator = checkProcessRole(user, response.getApplication(), COLLABORATOR, processRoleRepository);
            return isCollaborator || isLeadApplicant;
        }
        return false;
    }

    @PermissionRule(value = "READ", description = "The assessor can see the input responses of in applications for the applications they assess")
    public boolean assessorCanSeeTheInputResponsesInApplicationsTheyAssess(final FormInputResponseResource response, final UserResource user) {
        return checkProcessRole(user, response.getApplication(), ASSESSOR, processRoleRepository);
    }

    @PermissionRule(value = "READ", description = "Monitoring officers can see the input responses for the applications they are assigned to")
    public boolean monitoringOfficersCanSeeTheInputResponsesInApplicationsAssignedToThem(final FormInputResponseResource response, final UserResource user) {
        return monitoringOfficerCanViewApplication(response.getApplication(), user.getId());
    }

    @PermissionRule(value = "READ", description = "The assessor can see the input responses of in applications for the applications they review")
    public boolean assessorCanSeeTheInputResponsesInApplicationsTheyReview(final FormInputResponseResource response, final UserResource user) {
        return checkProcessRole(user, response.getApplication(), PANEL_ASSESSOR, processRoleRepository);
    }

    @PermissionRule(value = "READ", description = "The assessor can see the input responses of in applications for the applications they interview")
    public boolean assessorCanSeeTheInputResponsesInApplicationsTheyInterview(final FormInputResponseResource response, final UserResource user) {
        return checkProcessRole(user, response.getApplication(), INTERVIEW_ASSESSOR, processRoleRepository);
    }

    @PermissionRule(value = "READ", description = "An internal user can see form input responses for applications")
    public boolean internalUserCanSeeFormInputResponsesForApplications(final FormInputResponseResource response, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ", description = "Stakeholders can see form input responses for applications they are assigned to")
    public boolean stakeholdersCanSeeFormInputResponsesForApplications(final FormInputResponseResource response, final UserResource user) {
        Application application = applicationRepository.findById(response.getApplication()).get();
        return userIsStakeholderInCompetition(application.getCompetition().getId(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Competition finance users can see form input responses for applications they are assigned to")
    public boolean competitionFinanceUsersCanSeeFormInputResponsesForApplications(final FormInputResponseResource response, final UserResource user) {
        Application application = applicationRepository.findById(response.getApplication()).get();
        return userIsExternalFinanceInCompetition(application.getCompetition().getId(), user.getId());
    }

    @PermissionRule(value = "SAVE",
            description = "A consortium member can update the response.")
    public boolean aConsortiumMemberCanUpdateAFormInputResponse(final FormInputResponseCommand response, final UserResource user) {
        final long applicationId = response.getApplicationId();
        final boolean isLead = checkProcessRole(user, applicationId, LEADAPPLICANT, processRoleRepository);
        final boolean isCollaborator = checkProcessRole(user, applicationId, COLLABORATOR, processRoleRepository);

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
        FormInput formInput = formInputRepository.findById(responseCommand.getFormInputId()).get();
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

    private boolean checkRoleForApplicationAndOrganisation(UserResource user, FormInputResponseResource response, Role userRoleType) {
        final Long organisationId = processRoleRepository.findById(response.getUpdatedBy()).get().getOrganisationId();
        final Long applicationId = response.getApplication();
        return checkProcessRole(user, applicationId, organisationId, userRoleType, processRoleRepository);
    }
}