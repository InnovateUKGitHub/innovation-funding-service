package org.innovateuk.ifs.application.viewmodel.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

import java.util.List;
import java.util.Optional;

/**
 * View model for your funding section.
 */
public class YourFundingSectionViewModel extends AbstractSectionViewModel {
    private boolean complete;

    public YourFundingSectionViewModel(ApplicantSectionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel, boolean allReadOnly, Optional<Long> applicantOrganisationId) {
        super(applicantResource, formInputViewModels, navigationViewModel, allReadOnly, applicantOrganisationId);
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}

