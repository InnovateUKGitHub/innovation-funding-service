package org.innovateuk.ifs.application.security;

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
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.security.SecurityRuleUtil.checkProcessRole;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.*;

@PermissionRules
@Component
public class FormInputResponsePermissionRules extends BasePermissionRules {

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Autowired
    private ApplicationSecurityHelper applicationSecurityHelper;

    @PermissionRule(value = "READ", description = "A user can see the response if they can view the application")
    public boolean userCanSeeResponseIfTheyCanViewApplication(final FormInputResponseResource response, final UserResource user) {
        return applicationSecurityHelper.canViewApplication(response.getApplication(), user);
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
}