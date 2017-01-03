package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Map;

/**
 * TODO - comments that make sense
 */
public class QuestionViewModel {
    private UserResource currentUser;
    private Map<Long, List<FormInputResource>> questionFormInputs;
    private String title;
    private QuestionResource currentQuestion;

    private QuestionApplicationViewModel questionApplicationViewModel;
    private QuestionNavigationViewModel questionNavigationViewModel;

    public QuestionViewModel(UserResource currentUser, Map<Long, List<FormInputResource>> questionFormInputs, String title, QuestionResource currentQuestion,
                             QuestionApplicationViewModel questionApplicationViewModel, QuestionNavigationViewModel questionNavigationViewModel) {
        this.currentUser = currentUser;
        this.questionFormInputs = questionFormInputs;
        this.title = title;
        this.currentQuestion = currentQuestion;
        this.questionApplicationViewModel = questionApplicationViewModel;
        this.questionNavigationViewModel = questionNavigationViewModel;
    }

    public UserResource getCurrentUser() {
        return currentUser;
    }

    public Map<Long, List<FormInputResource>> getQuestionFormInputs() {
        return questionFormInputs;
    }

    public String getTitle() {
        return title;
    }

    public QuestionResource getCurrentQuestion() {
        return currentQuestion;
    }

    public QuestionApplicationViewModel getApplication() {
        return getQuestionApplicationViewModel();
    }

    public QuestionApplicationViewModel getQuestionApplicationViewModel() {
        return questionApplicationViewModel;
    }
}
