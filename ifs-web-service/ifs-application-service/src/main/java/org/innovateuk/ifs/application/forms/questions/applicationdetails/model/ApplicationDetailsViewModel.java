package org.innovateuk.ifs.application.forms.questions.applicationdetails.model;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.application.viewmodel.AbstractApplicationFormViewModel;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

import java.util.List;
import java.util.Optional;

/**
 * View model for application details.
 */
public class ApplicationDetailsViewModel extends AbstractApplicationFormViewModel<ApplicantQuestionResource> {

    public ApplicationDetailsViewModel(ApplicantQuestionResource applicantResource,
                                       List<AbstractFormInputViewModel> formInputViewModels,
                                       NavigationViewModel navigationViewModel,
                                       boolean allReadOnly,
                                       Optional<Long> applicantOrganisationId,
                                       boolean readOnlyAllApplicantApplicationFinances) {
        super(applicantResource, formInputViewModels, navigationViewModel, allReadOnly, applicantOrganisationId, readOnlyAllApplicantApplicationFinances);
    }

    public NavigationViewModel getNavigation() {
        return getNavigationViewModel();
    }

    @Override
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
