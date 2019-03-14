package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * ViewModel for Finance open sections
 */
public class OpenFinanceSectionViewModel extends BaseSectionViewModel {

    private boolean notRequestingFunding;
    private SectionResource fundingSection;

    public OpenFinanceSectionViewModel(NavigationViewModel navigationViewModel, SectionResource currentSection,
                                       Boolean hasFinanceSection, Long financeSectionId, UserResource currentUser,
                                       Boolean subFinanceSection) {
        this.navigationViewModel = navigationViewModel;
        this.currentSection = currentSection;
        this.hasFinanceSection = hasFinanceSection;
        this.financeSectionId = financeSectionId;
        this.currentUser = currentUser;
        this.subFinanceSection = subFinanceSection;
    }

    public void setNotRequestingFunding(boolean notRequestingFunding) {
        this.notRequestingFunding = notRequestingFunding;
    }

    /* Your finances display logic */
    public boolean showSectionAsNotRequired(SectionResource subSection) {
        return notRequestingFunding && (SectionType.ORGANISATION_FINANCES.equals(subSection.getType())
                || SectionType.FUNDING_FINANCES.equals(subSection.getType()));
    }

    public void setFundingSection(SectionResource fundingSection) {
        this.fundingSection = fundingSection;
    }
}