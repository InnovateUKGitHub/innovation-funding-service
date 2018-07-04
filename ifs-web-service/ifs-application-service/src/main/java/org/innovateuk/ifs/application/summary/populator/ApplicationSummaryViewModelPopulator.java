package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.common.populator.SummaryViewModelPopulator;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class ApplicationSummaryViewModelPopulator {

    private ApplicationService applicationService;
    private CompetitionService competitionService;
    private UserService userService;
    private SummaryViewModelPopulator summaryViewModelPopulator;
    private ProjectService projectService;

    public ApplicationSummaryViewModelPopulator(ApplicationService applicationService,
                                                CompetitionService competitionService,
                                                UserService userService,
                                                SummaryViewModelPopulator summaryViewModelPopulator,
                                                ProjectService projectService) {
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.userService = userService;
        this.summaryViewModelPopulator = summaryViewModelPopulator;
        this.projectService = projectService;
    }

    public ApplicationSummaryViewModel populate (long applicationId, UserResource user, ApplicationForm form) {
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        return new ApplicationSummaryViewModel(
                application,
                competition,
                applicationService.isApplicationReadyForSubmit(application.getId()),
                summaryViewModelPopulator.populate(applicationId, user, form),
                userService.isLeadApplicant(user.getId(), application),
                isProjectWithDrawn(applicationId));
    }

    private boolean isProjectWithDrawn(Long applicationId) {
        ProjectResource project = projectService.getByApplicationId(applicationId);
        return project != null && project.isWithdrawn();
    }
}
