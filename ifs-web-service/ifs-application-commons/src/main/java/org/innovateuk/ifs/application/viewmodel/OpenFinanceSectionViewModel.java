package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.finance.viewmodel.AcademicFinanceViewModel;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * ViewModel for Finance open sections
 */
public class OpenFinanceSectionViewModel extends BaseSectionViewModel {
    private static final List<SectionType> ACADEMIC_FINANCE_SUB_SECTIONS = asList(SectionType.PROJECT_COST_FINANCES);
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

    public boolean isNotRequestingFunding() {
        return notRequestingFunding;
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

    public Boolean getOrganisationSizeAlert() {
        if(null == fundingSection) {
            return Boolean.FALSE;
        }

        return sectionsMarkedAsComplete.contains(fundingSection.getId());
    }

    public boolean isSectionDisplayed(SectionResource subSection) {
        return !getIsAcademicFinance() || ACADEMIC_FINANCE_SUB_SECTIONS.contains(subSection.getType());
    }

    public boolean getIsAcademicFinance() {
        return (getFinanceViewModel() instanceof AcademicFinanceViewModel);
    }
}