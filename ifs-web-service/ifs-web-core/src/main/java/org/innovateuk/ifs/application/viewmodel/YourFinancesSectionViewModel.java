package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

import java.util.List;

/**
 * Generic ViewModel for common fields in SectionViewModels
 */
public class YourFinancesSectionViewModel extends AbstractSectionViewModel {

    public YourFinancesSectionViewModel(ApplicantSectionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel) {
        super(applicantResource, formInputViewModels, navigationViewModel);
    }


}
