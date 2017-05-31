package org.innovateuk.ifs.application.viewmodel.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

import java.util.List;

/**
 * Default your project costs view model.
 */
public class DefaultYourProjectCostsSectionViewModel extends AbstractYourProjectCostsSectionViewModel {

    private List<DefaultProjectCostSection> defaultProjectCostSections;

    public DefaultYourProjectCostsSectionViewModel(ApplicantSectionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel, boolean allReadOnly) {
        super(applicantResource, formInputViewModels, navigationViewModel, allReadOnly);
    }

    @Override
    public String getFinanceView() {
        return "finance";
    }

    public List<DefaultProjectCostSection> getDefaultProjectCostSections() {
        return defaultProjectCostSections;
    }

    public void setDefaultProjectCostSections(List<DefaultProjectCostSection> defaultProjectCostSections) {
        this.defaultProjectCostSections = defaultProjectCostSections;
    }
}

