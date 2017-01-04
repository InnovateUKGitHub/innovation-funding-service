package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * TODO - create some comments
 */
public class OpenFinanceSectionViewModel extends BaseSectionViewModel {
    public OpenFinanceSectionViewModel(NavigationViewModel navigationViewModel, Boolean allReadOny, ApplicationResource currentApplication,
                                       CompetitionResource currentCompetition, SectionResource currentSection,
                                       Boolean hasFinanceSection, Long financeSectionId, UserResource currentUser) {
        setNavigationViewModel(navigationViewModel);
        setAllReadOnly(allReadOny);
        setCurrentApplication(currentApplication);
        setCurrentCompetition(currentCompetition);
        setCurrentSection(currentSection);
        setHasFinanceSection(hasFinanceSection);
        setFinanceSectionId(financeSectionId);
        setCurrentUser(currentUser);
    }
}
