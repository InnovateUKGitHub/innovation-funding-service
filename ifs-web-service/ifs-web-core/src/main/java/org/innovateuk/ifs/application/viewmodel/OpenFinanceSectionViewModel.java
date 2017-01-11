package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * ViewModel for Finance open sections
 */
public class OpenFinanceSectionViewModel extends BaseSectionViewModel {

    private boolean subFinanceSection;

    public OpenFinanceSectionViewModel(NavigationViewModel navigationViewModel, SectionResource currentSection,
                                       Boolean hasFinanceSection, Long financeSectionId, UserResource currentUser,
                                       boolean subFinanceSection) {
        setNavigationViewModel(navigationViewModel);
        setCurrentSection(currentSection);
        setHasFinanceSection(hasFinanceSection);
        setFinanceSectionId(financeSectionId);
        setCurrentUser(currentUser);
        this.subFinanceSection = subFinanceSection;
    }

    public boolean isSubFinanceSection() {
        return subFinanceSection;
    }

    public void setSubFinanceSection(boolean subFinanceSection) {
        this.subFinanceSection = subFinanceSection;
    }
}
