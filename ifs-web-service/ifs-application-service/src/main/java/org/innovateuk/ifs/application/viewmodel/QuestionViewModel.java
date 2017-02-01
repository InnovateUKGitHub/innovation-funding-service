package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * ViewModel for questions in the applications
 */
public class QuestionViewModel {
    private UserResource currentUser;
    private Map<Long, List<FormInputResource>> questionFormInputs;
    private String title;
    private QuestionResource currentQuestion;

    private Map<Long, FormInputResponseResource> responses;
    private Boolean userIsLeadApplicant;
    private UserResource leadApplicant;

    private SortedSet<OrganisationResource> academicOrganisations;
    private SortedSet<OrganisationResource> applicationOrganisations;
    private List<String> pendingOrganisationNames;
    private OrganisationResource leadOrganisation;

    private QuestionApplicationViewModel questionApplicationViewModel;
    private NavigationViewModel navigationViewModel;
    private QuestionAssignableViewModel questionAssignableViewModel;

    public QuestionViewModel() {}

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

    public void setQuestionFormInputs(Map<Long, List<FormInputResource>> questionFormInputs) {
        this.questionFormInputs = questionFormInputs;
    }

    public List<FormInputResource> getCurrentQuestionFormInputs() {
        if(null != currentQuestion) {
            return questionFormInputs.get(currentQuestion.getId());
        }
        return Collections.emptyList();
    }

    public Boolean getHasCurrentQuestionFormInputs() {
        return null != questionFormInputs && !questionFormInputs.isEmpty();
    }

    public SortedSet<OrganisationResource> getAcademicOrganisations() {
        return academicOrganisations;
    }

    public void setAcademicOrganisations(SortedSet<OrganisationResource> academicOrganisations) {
        this.academicOrganisations = academicOrganisations;
    }

    public SortedSet<OrganisationResource> getApplicationOrganisations() {
        return applicationOrganisations;
    }

    public void setApplicationOrganisations(SortedSet<OrganisationResource> applicationOrganisations) {
        this.applicationOrganisations = applicationOrganisations;
    }

    public List<String> getPendingOrganisationNames() {
        return pendingOrganisationNames;
    }

    public void setPendingOrganisationNames(List<String> pendingOrganisationNames) {
        this.pendingOrganisationNames = pendingOrganisationNames;
    }

    public OrganisationResource getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(OrganisationResource leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
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

    public Boolean getIsSection() {
        return Boolean.FALSE;
    }

    public Boolean isShowReturnButtons() {
        return Boolean.TRUE;
    }
}
