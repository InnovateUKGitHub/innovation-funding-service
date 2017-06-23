package org.innovateuk.ifs.application.viewmodel.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

import java.util.List;
import java.util.Optional;

/**
 * View model for the finance overview page.
 */
public class FinanceOverviewSectionViewModel extends AbstractSectionViewModel {

    private OpenSectionViewModel openSectionViewModel;

    public FinanceOverviewSectionViewModel(ApplicantSectionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel, boolean allReadOnly, Optional<Long> applicantOrganisationId, boolean readOnlyAllApplicantApplicationFinances) {
        super(applicantResource, formInputViewModels, navigationViewModel, allReadOnly, applicantOrganisationId, readOnlyAllApplicantApplicationFinances);
    }

    public OpenSectionViewModel getOpenSectionViewModel() {
        return openSectionViewModel;
    }

    public void setOpenSectionViewModel(OpenSectionViewModel openSectionViewModel) {
        this.openSectionViewModel = openSectionViewModel;
    }
}

