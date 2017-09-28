package org.innovateuk.ifs.competitionsetup.viewmodel;

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
}
