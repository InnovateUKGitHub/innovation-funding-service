package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

import java.util.List;

/**
 * View model extending the {@link AbstractSectionViewModel} for open sections (not finance, but used by finances overview)
 */
public class OpenSectionViewModel extends AbstractSectionViewModel {
    public OpenSectionViewModel(ApplicantSectionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel) {
        super(applicantResource, formInputViewModels, navigationViewModel);
    }
}
