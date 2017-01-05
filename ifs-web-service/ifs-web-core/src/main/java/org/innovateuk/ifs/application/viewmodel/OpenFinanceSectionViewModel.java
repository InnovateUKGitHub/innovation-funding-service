package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * TODO - create some comments
 */
public class OpenFinanceSectionViewModel extends BaseSectionViewModel {
    public OpenFinanceSectionViewModel(NavigationViewModel navigationViewModel, SectionResource currentSection,
                                       Boolean hasFinanceSection, Long financeSectionId, UserResource currentUser) {
        setNavigationViewModel(navigationViewModel);
        setCurrentSection(currentSection);
        setHasFinanceSection(hasFinanceSection);
        setFinanceSectionId(financeSectionId);
        setCurrentUser(currentUser);
    }
}
