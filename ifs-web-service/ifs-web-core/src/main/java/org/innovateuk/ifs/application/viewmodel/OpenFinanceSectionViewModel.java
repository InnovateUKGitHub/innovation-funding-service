package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.application.viewmodel.section.AbstractSectionViewModel;

import java.util.List;

/**
 * ViewModel for Finance open sections
 */
public class OpenFinanceSectionViewModel extends AbstractSectionViewModel {
    public OpenFinanceSectionViewModel(ApplicantSectionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel, boolean allReadOnly) {
        super(applicantResource, formInputViewModels, navigationViewModel, allReadOnly);
    }
}
