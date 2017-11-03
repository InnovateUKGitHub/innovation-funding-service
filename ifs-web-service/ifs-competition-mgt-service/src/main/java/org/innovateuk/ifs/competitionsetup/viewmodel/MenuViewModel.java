package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

public class MenuViewModel extends CompetitionSetupViewModel {

    private ZonedDateTime publishDate;
    private boolean isPublicContentPublished;
    private Map<CompetitionSetupSection, Optional<Boolean>> statuses;

    public MenuViewModel(GeneralSetupViewModel generalSetupViewModel, ZonedDateTime publishDate,
                         boolean isPublicContentPublished,
                         Map<CompetitionSetupSection, Optional<Boolean>> statuses) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.publishDate = publishDate;
        this.isPublicContentPublished = isPublicContentPublished;
        this.statuses = statuses;
    }

    public ZonedDateTime getPublishDate() {
        return publishDate;
    }

    public boolean isPublicContentPublished() {
        return isPublicContentPublished;
    }

    public boolean initialCompleteOrSectionIsInitial(CompetitionSetupSection setupSection) {
        return generalSetupViewModel.isInitialComplete() || setupSection.equals(CompetitionSetupSection.INITIAL_DETAILS);
    }

    public boolean publicContentPublishedAndSectionIsContent(CompetitionSetupSection setupSection) {
        return isPublicContentPublished() && setupSection.equals(CompetitionSetupSection.CONTENT);
    }

    public boolean sectionCompleteAndCompetitionNotLive(CompetitionSetupSection setupSection) {
        return statuses.get(setupSection).orElse(Boolean.FALSE) && !generalSetupViewModel.getCompetition().isSetupAndLive();
    }

    public boolean sectionIsNotHome(CompetitionSetupSection setupSection) {
        return !setupSection.equals(CompetitionSetupSection.HOME);
    }
}
