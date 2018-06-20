package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.areas.populator.ApplicationResearchCategoryModelPopulator;
import org.innovateuk.ifs.application.areas.viewmodel.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.application.team.populator.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class ApplicationSummaryViewModelPopulator {

    private ApplicationService applicationService;
    private CompetitionService competitionService;
    private UserService userService;
    private SummaryViewModelPopulator summaryViewModelPopulator;
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;
    private ApplicationResearchCategoryModelPopulator researchCategoryModelPopulator;
    private ProjectService projectService;

    public ApplicationSummaryViewModelPopulator(ApplicationService applicationService,
                                                CompetitionService competitionService,
                                                UserService userService,
                                                SummaryViewModelPopulator summaryViewModelPopulator,
                                                ApplicationTeamModelPopulator applicationTeamModelPopulator,
                                                ApplicationResearchCategoryModelPopulator
                                                        researchCategoryModelPopulator,
                                                ProjectService projectService) {
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.userService = userService;
        this.summaryViewModelPopulator = summaryViewModelPopulator;
        this.applicationTeamModelPopulator = applicationTeamModelPopulator;
        this.researchCategoryModelPopulator = researchCategoryModelPopulator;
        this.projectService = projectService;
    }

    public ApplicationSummaryViewModel populate(long applicationId, UserResource user, ApplicationForm form) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        ProjectResource project = projectService.getByApplicationId(applicationId);
        boolean projectWithdrawn = (project != null && project.isWithdrawn());

        boolean applicationReadyForSubmit = applicationService.isApplicationReadyForSubmit(application.getId());

        SummaryViewModel summaryViewModel = summaryViewModelPopulator.populate(applicationId, user, form);
        ApplicationTeamViewModel applicationTeamViewModel = applicationTeamModelPopulator.populateSummaryModel
                (applicationId, user.getId(), application.getCompetition());
        ResearchCategorySummaryViewModel researchCategorySummaryViewModel = researchCategoryModelPopulator
                .populateSummaryViewModel(application);

        Boolean userIsLeadApplicant = userService.isLeadApplicant(user.getId(), application);

        return new ApplicationSummaryViewModel(
                application,
                competition,
                applicationReadyForSubmit,
                summaryViewModel,
                applicationTeamViewModel,
                researchCategorySummaryViewModel,
                userIsLeadApplicant,
                projectWithdrawn);
    }
}
