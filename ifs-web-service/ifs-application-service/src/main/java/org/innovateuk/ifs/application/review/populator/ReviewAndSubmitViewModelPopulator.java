package org.innovateuk.ifs.application.review.populator;

import org.innovateuk.ifs.application.review.viewmodel.ReviewAndSubmitViewModel;
import org.innovateuk.ifs.application.summary.populator.NewApplicationSummaryViewModelPopulator;
import org.innovateuk.ifs.application.summary.viewmodel.NewApplicationSummaryViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.application.summary.ApplicationSummarySettings.defaultSettings;

@Component
public class ReviewAndSubmitViewModelPopulator {

    @Autowired
    private NewApplicationSummaryViewModelPopulator applicationSummaryViewModelPopulator;

    public ReviewAndSubmitViewModel populate(long applicationId, UserResource user) {
        NewApplicationSummaryViewModel applicationSummaryViewModel = applicationSummaryViewModelPopulator.populate(applicationId, user, defaultSettings()
                .setIncludeQuestionLinks(true)
                .setIncludeStatuses(true));
        return new ReviewAndSubmitViewModel(applicationSummaryViewModel, applicationId);
    }
}
