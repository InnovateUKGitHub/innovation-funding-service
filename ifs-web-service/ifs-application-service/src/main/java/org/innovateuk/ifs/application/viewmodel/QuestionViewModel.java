package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

import java.util.List;

/**
 * ViewModel for questions in the applications
 */
public class QuestionViewModel extends AbstractApplicantViewModel<ApplicantQuestionResource> {
    private QuestionAssignableViewModel questionAssignableViewModel;
    private boolean allReadOnly;

    public QuestionViewModel(ApplicantQuestionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel, QuestionAssignableViewModel questionAssignableViewModel, boolean allReadOnly) {
        super(applicantResource, formInputViewModels, navigationViewModel);
        this.questionAssignableViewModel = questionAssignableViewModel;
        this.allReadOnly = allReadOnly;
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

    public boolean isAllReadOnly() {
        return allReadOnly;
    }

    @Override
    public String getTitle() {
        return applicantResource.getQuestion().getShortName();
    }
}
