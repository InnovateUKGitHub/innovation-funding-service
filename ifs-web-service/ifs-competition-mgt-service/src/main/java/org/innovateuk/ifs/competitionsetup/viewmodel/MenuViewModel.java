package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;

import java.time.ZonedDateTime;

public class MenuViewModel extends CompetitionSetupViewModel {
    private ZonedDateTime publishDate;
    private boolean isPublicContentPublished;

    public MenuViewModel(GeneralSetupViewModel generalSetupViewModel, ZonedDateTime publishDate, boolean isPublicContentPublished) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.publishDate = publishDate;
        this.isPublicContentPublished = isPublicContentPublished;
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
        return setupSection.isComplete(generalSetupViewModel.getCompetition()) && !generalSetupViewModel.getCompetition().isSetupAndLive();
    }

    public boolean sectionIsNotHome(CompetitionSetupSection setupSection) {
        return !setupSection.equals(CompetitionSetupSection.HOME);
    }
}
