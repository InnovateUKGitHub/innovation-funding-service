package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

import java.util.List;

/**
 * Generic ViewModel for common fields in SectionViewModels
 */
public abstract class AbstractSectionViewModel extends AbstractApplicantViewModel<ApplicantSectionResource> {

    public AbstractSectionViewModel(ApplicantSectionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel) {
        super(applicantResource, formInputViewModels, navigationViewModel);
    }

    public String getTitle() {
        return applicantResource.getSection().getName();
    }

}
