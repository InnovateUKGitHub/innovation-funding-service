package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;

import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractQuestionReadOnlyViewModel implements ApplicationQuestionReadOnlyViewModel, BaseAnalyticsViewModel {

    private final long applicationId;
    private final String competitionName;
    private final long questionId;
    private final String name;
    private final boolean complete;
    private final boolean displayActions;
    private final boolean lead;
    private final boolean ktpCompetition;

    public AbstractQuestionReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource question) {
        this.competitionName = data.getCompetition().getName();
        this.name = question.getShortName();
        this.applicationId = data.getApplication().getId();
        this.questionId = question.getId();
        this.lead = data.getApplicantProcessRole().map(role -> Role.LEADAPPLICANT == role.getRole()).orElse(false);
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
                .map(isAssignedToProcessRole(data.getApplicantProcessRole()))
                .orElse(false);
        this.displayActions = lead || assignedToUser;
        this.ktpCompetition = data.getCompetition().isKtp();
    }

    private Function<QuestionStatusResource, Boolean> isAssignedToProcessRole(Optional<ProcessRoleResource> processRole) {
        return status ->  processRole.isPresent() && status.getAssignee().equals(processRole.get().getId());
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
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

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }
}
