package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;

@Component
public class ApplicationSummaryViewModelPopulator {

    @Autowired
    private ApplicationReadOnlyViewModelPopulator applicationReadOnlyViewModelPopulator;

    @Autowired
    private ProjectService projectService;

    public ApplicationSummaryViewModel populate(ApplicationResource application, CompetitionResource competition, UserResource user) {
        ApplicationReadOnlySettings settings = defaultSettings();
        ApplicationReadOnlyViewModel applicationReadOnlyViewModel = applicationReadOnlyViewModelPopulator.populate(application, competition, user, settings);
        return new ApplicationSummaryViewModel(applicationReadOnlyViewModel,
                                               application,
                                               competition,
                                               isProjectWithdrawn(application.getId()));
    }

    private boolean isProjectWithdrawn(Long applicationId) {
        ProjectResource project = projectService.getByApplicationId(applicationId);
        return project != null && project.isWithdrawn();
    }
}
