package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.forms.researchcategory.populator.ApplicationResearchCategorySummaryModelPopulator;
import org.innovateuk.ifs.application.forms.researchcategory.viewmodel.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.application.common.populator.SummaryViewModelPopulator;
import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationSummaryViewModel;
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
    private ApplicationResearchCategorySummaryModelPopulator researchCategorySummaryModelPopulator;
    private ProjectService projectService;

    public ApplicationSummaryViewModelPopulator(ApplicationService applicationService,
                                                CompetitionService competitionService,
                                                UserService userService,
                                                SummaryViewModelPopulator summaryViewModelPopulator,
                                                ApplicationTeamModelPopulator applicationTeamModelPopulator,
                                                ApplicationResearchCategorySummaryModelPopulator
                                                        researchCategorySummaryModelPopulator,
                                                ProjectService projectService) {
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.userService = userService;
        this.summaryViewModelPopulator = summaryViewModelPopulator;
        this.applicationTeamModelPopulator = applicationTeamModelPopulator;
        this.researchCategorySummaryModelPopulator = researchCategorySummaryModelPopulator;
        this.projectService = projectService;
    }

    public ApplicationSummaryViewModel populate(long applicationId, UserResource user, ApplicationForm form) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        ProjectResource project = projectService.getByApplicationId(applicationId);
        boolean projectWithdrawn = (project != null && project.isWithdrawn());

        boolean applicationReadyForSubmit = applicationService.isApplicationReadyForSubmit(application.getId());
        boolean userIsLeadApplicant = userService.isLeadApplicant(user.getId(), application);

        SummaryViewModel summaryViewModel = summaryViewModelPopulator.populate(applicationId, user, form);

        ApplicationTeamViewModel applicationTeamViewModel = competition.getUseNewApplicantMenu() ?
                applicationTeamModelPopulator.populateSummaryModel(applicationId, user.getId(), application.getCompetition()) : null;

        ResearchCategorySummaryViewModel researchCategorySummaryViewModel = competition.getUseNewApplicantMenu() ?
                researchCategorySummaryModelPopulator.populate(application, user.getId(), userIsLeadApplicant) : null;

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
