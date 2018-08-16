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
    private boolean researchCategoryRequired;
    private boolean yourOrganisationRequired;
    private Long researchCategoryQuestionId;
    private long yourOrganisationSectionId;

    public YourFundingSectionViewModel(ApplicantSectionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel, boolean allReadOnly, Optional<Long> applicantOrganisationId, boolean readOnlyAllApplicantApplicationFinances) {
        super(applicantResource, formInputViewModels, navigationViewModel, allReadOnly, applicantOrganisationId, readOnlyAllApplicantApplicationFinances);
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isFundingSectionLocked() {
        return this.researchCategoryRequired || this.yourOrganisationRequired;
    }

    public boolean isResearchCategoryRequired() {
        return researchCategoryRequired;
    }

    public void setResearchCategoryRequired(final boolean researchCategoryRequired) {
        this.researchCategoryRequired = researchCategoryRequired;
    }

    public boolean isYourOrganisationRequired() {
        return yourOrganisationRequired;
    }

    public void setYourOrganisationRequired(final boolean yourOrganisationRequired) {
        this.yourOrganisationRequired = yourOrganisationRequired;
    }

    public Long getResearchCategoryQuestionId() {
        return researchCategoryQuestionId;
    }

    public void setResearchCategoryQuestionId(Long researchCategoryQuestionId) {
        this.researchCategoryQuestionId = researchCategoryQuestionId;
    }

    public long getYourOrganisationSectionId() {
        return yourOrganisationSectionId;
    }

    public void setYourOrganisationSectionId(long yourOrganisationSectionId) {
        this.yourOrganisationSectionId = yourOrganisationSectionId;
    }
}

