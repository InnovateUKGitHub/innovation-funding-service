package org.innovateuk.ifs.application.viewmodel.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

import java.util.List;

/**
 * View model for the finance overview page.
 */
public class FinanceOverviewSectionViewModel extends AbstractSectionViewModel {

    private OpenSectionViewModel openSectionViewModel;

    public FinanceOverviewSectionViewModel(ApplicantSectionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel, boolean allReadOnly) {
        super(applicantResource, formInputViewModels, navigationViewModel, allReadOnly);
    }

    public OpenSectionViewModel getOpenSectionViewModel() {
        return openSectionViewModel;
    }

    public void setOpenSectionViewModel(OpenSectionViewModel openSectionViewModel) {
        this.openSectionViewModel = openSectionViewModel;
    }
}

