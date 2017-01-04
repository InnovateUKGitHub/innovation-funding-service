package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.Collections;
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

    private Map<Long, FormInputResponseResource> responses;
    private Boolean userIsLeadApplicant;
    private UserResource leadApplicant;

    private QuestionApplicationViewModel questionApplicationViewModel;
    private NavigationViewModel navigationViewModel;
    private QuestionAssignableViewModel questionAssignableViewModel;

    public QuestionViewModel(UserResource currentUser, Map<Long, List<FormInputResource>> questionFormInputs, String title, QuestionResource currentQuestion,
                             QuestionApplicationViewModel questionApplicationViewModel, NavigationViewModel navigationViewModel,
                             QuestionAssignableViewModel questionAssignableViewModel) {
        this.currentUser = currentUser;
        this.questionFormInputs = questionFormInputs;
        this.title = title;
        this.currentQuestion = currentQuestion;
        this.questionApplicationViewModel = questionApplicationViewModel;
        this.navigationViewModel = navigationViewModel;
        this.questionAssignableViewModel = questionAssignableViewModel;
    }

    public UserResource getCurrentUser() {
        return currentUser;
    }

    public Map<Long, List<FormInputResource>> getQuestionFormInputs() {
        return questionFormInputs;
    }

    public List<FormInputResource> getCurrentQuestionFormInputs() {
        if(null != currentQuestion) {
            return questionFormInputs.get(currentQuestion.getId());
        }
        return Collections.emptyList();
    }

    public String getTitle() {
        return title;
    }

    public QuestionResource getCurrentQuestion() {
        return currentQuestion;
    }

    public Map<Long, FormInputResponseResource> getResponses() {
        return responses;
    }

    public void setResponses(Map<Long, FormInputResponseResource> responses) {
        this.responses = responses;
    }

    public Boolean getUserIsLeadApplicant() {
        return userIsLeadApplicant;
    }

    public void setUserIsLeadApplicant(Boolean userIsLeadApplicant) {
        this.userIsLeadApplicant = userIsLeadApplicant;
    }

    public UserResource getLeadApplicant() {
        return leadApplicant;
    }

    public void setLeadApplicant(UserResource leadApplicant) {
        this.leadApplicant = leadApplicant;
    }

    public QuestionApplicationViewModel getApplication() {
        return getQuestionApplicationViewModel();
    }

    public QuestionApplicationViewModel getQuestionApplicationViewModel() {
        return questionApplicationViewModel;
    }

    public NavigationViewModel getNavigation() {
        return getNavigationViewModel();
    }

    public NavigationViewModel getNavigationViewModel() {
        return navigationViewModel;
    }

    public QuestionAssignableViewModel getAssignable() {
        return getQuestionAssignableViewModel();
    }

    public QuestionAssignableViewModel getQuestionAssignableViewModel() {
        return questionAssignableViewModel;
    }
}
