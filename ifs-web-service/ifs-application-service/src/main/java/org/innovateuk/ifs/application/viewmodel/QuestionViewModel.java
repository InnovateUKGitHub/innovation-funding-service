package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

import java.util.List;

/**
 * ViewModel for questions in the applications
 */
public class QuestionViewModel extends AbstractApplicationFormViewModel<ApplicantQuestionResource> {

    public QuestionViewModel(ApplicantQuestionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel, boolean allReadOnly) {
        super(applicantResource, formInputViewModels, navigationViewModel, allReadOnly);
    }


    public NavigationViewModel getNavigation() {
        return getNavigationViewModel();
    }

    public NavigationViewModel getNavigationViewModel() {
        return navigationViewModel;
    }

    public Boolean isShowReturnButtons() {
        return Boolean.TRUE;
    }

    @Override
    public String getTitle() {
        return applicantResource.getQuestion().getShortName();
    }
}
