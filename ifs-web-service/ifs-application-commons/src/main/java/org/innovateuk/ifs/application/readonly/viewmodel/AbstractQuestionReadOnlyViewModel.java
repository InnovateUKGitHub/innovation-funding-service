package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;

import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractQuestionReadOnlyViewModel implements ApplicationQuestionReadOnlyViewModel {

    private final long applicationId;
    private final long questionId;
    private final String name;
    private final boolean complete;
    private final boolean displayActions;
    private final boolean lead;

    public AbstractQuestionReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource question) {
        this.name = question.getShortName();
        this.applicationId = data.getApplication().getId();
        this.questionId = question.getId();
        this.lead = data.getProcessRole().map(role -> Role.LEADAPPLICANT == role.getRole()).orElse(false);
        Optional<QuestionStatusResource> completeStatus = data.getQuestionToQuestionStatus()
                .get(question.getId())
                .stream()
                .filter(status -> status.getMarkedAsComplete() != null)
                .findFirst();
        this.complete = completeStatus.map(QuestionStatusResource::getMarkedAsComplete).orElse(false);
        Optional<QuestionStatusResource> assignedStatus = data.getQuestionToQuestionStatus()
                .get(question.getId())
                .stream()
                .filter(status -> status.getAssignee() != null)
                .findFirst();
        boolean assignedToUser = assignedStatus
                .map(isAssignedToProcessRole(data.getProcessRole()))
                .orElse(false);
        this.displayActions = lead || assignedToUser;
    }

    private Function<QuestionStatusResource, Boolean> isAssignedToProcessRole(Optional<ProcessRoleResource> processRole) {
        return status ->  processRole.isPresent() && status.getAssignee().equals(processRole.get().getId());
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getQuestionId() {
        return questionId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public boolean shouldDisplayActions() {
        return displayActions;
    }

    @Override
    public boolean shouldDisplayMarkAsComplete() {
        return isLead() && !isComplete();
    }

    @Override
    public boolean isLead() {
        return lead;
    }

}
