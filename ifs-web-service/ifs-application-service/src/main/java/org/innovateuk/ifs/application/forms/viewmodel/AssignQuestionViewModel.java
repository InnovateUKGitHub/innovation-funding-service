package org.innovateuk.ifs.application.forms.viewmodel;

import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.List;

public class AssignQuestionViewModel {

    private final String applicationName;
    private final List<ProcessRoleResource> users;
    private final QuestionResource question;

    public AssignQuestionViewModel(String applicationName,
                                   List<ProcessRoleResource> users,
                                   QuestionResource question) {
        this.applicationName = applicationName;
        this.question = question;
        this.users = users;
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
