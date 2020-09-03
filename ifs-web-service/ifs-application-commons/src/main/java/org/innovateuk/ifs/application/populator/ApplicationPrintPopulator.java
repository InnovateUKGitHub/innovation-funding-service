package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;

@Component
public class ApplicationPrintPopulator {

    @Autowired
    private ApplicationReadOnlyViewModelPopulator applicationReadOnlyViewModelPopulator;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public String print(final Long applicationId,
                        Model model, UserResource user) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        ApplicationReadOnlySettings settings = defaultSettings()
                .setIncludeAllAssessorFeedback(userCanViewFeedback(user, competition));

        ApplicationReadOnlyViewModel applicationReadOnlyViewModel = applicationReadOnlyViewModelPopulator.populate(applicationId, user, settings);
        model.addAttribute("model", applicationReadOnlyViewModel);
        return "application/print";
    }

    private boolean userCanViewFeedback(UserResource user, CompetitionResource competition) {
        return user.hasRole(Role.PROJECT_FINANCE) && competition.isProcurement();
    }
}
