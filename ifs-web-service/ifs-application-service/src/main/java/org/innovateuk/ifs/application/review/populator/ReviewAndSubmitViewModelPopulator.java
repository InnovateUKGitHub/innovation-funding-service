package org.innovateuk.ifs.application.review.populator;

import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.review.viewmodel.ReviewAndSubmitViewModel;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;

@Component
public class ReviewAndSubmitViewModelPopulator {

    @Autowired
    private ApplicationReadOnlyViewModelPopulator applicationRowsSummaryViewModelPopulator;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private UserService userService;

    public ReviewAndSubmitViewModel populate(long applicationId, UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        boolean userIsLeadApplicant = userService.isLeadApplicant(user.getId(), application);
        boolean isApplicationReadyForSubmit = applicationRestService.isApplicationReadyForSubmit(applicationId).getSuccess();
        boolean displaySubmitWarning = competition.getCovidType() == null;

        ApplicationReadOnlyViewModel applicationSummaryViewModel = applicationRowsSummaryViewModelPopulator.populate(application, competition, user, defaultSettings()
                .setIncludeQuestionLinks(true)
                .setIncludeStatuses(true));
        return new ReviewAndSubmitViewModel(applicationSummaryViewModel, application, competition,
                isApplicationReadyForSubmit, userIsLeadApplicant, displaySubmitWarning);
    }
}
