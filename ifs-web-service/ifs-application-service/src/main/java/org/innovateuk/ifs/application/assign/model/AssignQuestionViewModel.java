package org.innovateuk.ifs.application.assign.model;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.origin.AssignQuestionOrigin;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.List;

import static org.innovateuk.ifs.origin.AssignQuestionOrigin.OVERVIEW;

public class AssignQuestionViewModel {

    private final String applicationName;
    private final long applicationId;
    private final List<ProcessRoleResource> users;
    private final QuestionResource question;
    private final String originQuery;
    private final AssignQuestionOrigin originType;

    public AssignQuestionViewModel(ApplicationResource application,
                                   List<ProcessRoleResource> users,
                                   QuestionResource question,
                                   String originQuery,
                                   AssignQuestionOrigin originType) {
        this.applicationName = application.getName();
        this.applicationId = application.getId();
        this.question = question;
        this.users = users;
        this.originQuery = originQuery;
        this.originType = originType;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public QuestionResource getQuestion() {
        return question;
    }

    public List<ProcessRoleResource> getUsers() {
        return users;
    }

    public String getOriginQuery() {
        return originQuery;
    }

    public String getBackLinkTitle() {
        return originType.getTitle();
    }

    public boolean isFromOverview() {
        return originType == OVERVIEW;
    }
}
