package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

import java.util.List;

/**
 * ViewModel for Finance open sections
 */
public class OpenFinanceSectionViewModel extends AbstractSectionViewModel {
    public OpenFinanceSectionViewModel(ApplicantSectionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel) {
        super(applicantResource, formInputViewModels, navigationViewModel);
    }
}
