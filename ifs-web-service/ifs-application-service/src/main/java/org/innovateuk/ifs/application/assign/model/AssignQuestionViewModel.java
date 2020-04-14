package org.innovateuk.ifs.application.assign.model;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.List;

public class AssignQuestionViewModel implements BaseAnalyticsViewModel {

    private final String applicationName;
    private final long applicationId;
    private final String competitionName;
    private final List<ProcessRoleResource> users;
    private final QuestionResource question;

    public AssignQuestionViewModel(ApplicationResource application,
                                   List<ProcessRoleResource> users,
                                   QuestionResource question) {
        this.applicationId = application.getId();
        this.competitionName = getCompetitionName();
        this.applicationName = application.getName();
        this.question = question;
        this.users = users;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public QuestionResource getQuestion() {
        return question;
    }

    public List<ProcessRoleResource> getUsers() {
        return users;
    }
}
