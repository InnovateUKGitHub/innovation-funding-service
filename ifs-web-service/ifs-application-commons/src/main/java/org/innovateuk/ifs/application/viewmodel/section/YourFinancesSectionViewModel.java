package org.innovateuk.ifs.application.viewmodel.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;

import java.util.List;
import java.util.Optional;

/**
 * Generic ViewModel for common fields in SectionViewModels
 */
public class YourFinancesSectionViewModel extends AbstractSectionViewModel {
    private List<Long> completedSectionIds;
    private BaseFinanceResource organisationFinance;

    public YourFinancesSectionViewModel(ApplicantSectionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel, boolean allReadOnly, Optional<Long> applicantOrganisationId, boolean readOnlyAllApplicantApplicationFinances) {
        super(applicantResource, formInputViewModels, navigationViewModel, allReadOnly, applicantOrganisationId, readOnlyAllApplicantApplicationFinances);
    }

    public List<Long> getCompletedSectionIds() {
        return completedSectionIds;
    }

    public void setCompletedSectionIds(List<Long> completedSectionIds) {
        this.completedSectionIds = completedSectionIds;
    }

    public BaseFinanceResource getOrganisationFinance() {
        return organisationFinance;
    }

    public void setOrganisationFinance(BaseFinanceResource organisationFinance) {
        this.organisationFinance = organisationFinance;
    }

    public boolean isCanNotRequestFunding() {
        return !applicantResource.getCurrentApplicant().isResearch() && getApplicationIsReadOnly();
    }

    public boolean isSectionDisplayed(ApplicantSectionResource section) {
        return true;
    }

    public boolean isUserIsLeadApplicant() {
        return applicantResource.getCurrentApplicant().isLead();
    }
}