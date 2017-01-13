package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * ViewModel for Finance open sections
 */
public class OpenFinanceSectionViewModel extends BaseSectionViewModel {

    private boolean fundingSectionLocked;
    private Long applicationDetailsQuestionId;
    private Long yourOrganisationSectionId;


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

    public boolean isFundingSectionLocked() {
        return fundingSectionLocked;
    }

    public void setFundingSectionLocked(boolean fundingSectionLocked) {
        this.fundingSectionLocked = fundingSectionLocked;
    }

    public Long getApplicationDetailsQuestionId() {
        return applicationDetailsQuestionId;
    }

    public void setApplicationDetailsQuestionId(Long applicationDetailsQuestionId) {
        this.applicationDetailsQuestionId = applicationDetailsQuestionId;
    }

    public Long getYourOrganisationSectionId() {
        return yourOrganisationSectionId;
    }

    public void setYourOrganisationSectionId(Long yourOrganisationSectionId) {
        this.yourOrganisationSectionId = yourOrganisationSectionId;
    }
}
