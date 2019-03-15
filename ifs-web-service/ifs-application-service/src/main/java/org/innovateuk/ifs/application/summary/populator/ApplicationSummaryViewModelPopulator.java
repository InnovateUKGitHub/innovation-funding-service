package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.common.populator.SummaryViewModelFragmentPopulator;
import org.innovateuk.ifs.application.populator.granttransfer.GrantTransferSummaryPopulator;
import org.innovateuk.ifs.application.populator.researchCategory.ApplicationResearchCategorySummaryModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationSummaryViewModel;
import org.innovateuk.ifs.application.team.populator.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamViewModel;
import org.innovateuk.ifs.application.viewmodel.granttransfer.GrantAgreementSummaryViewModel;
import org.innovateuk.ifs.application.viewmodel.granttransfer.GrantTransferDetailsSummaryViewModel;
import org.innovateuk.ifs.application.viewmodel.researchCategory.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class ApplicationSummaryViewModelPopulator {

    private ApplicationService applicationService;
    private ApplicationRestService applicationRestService;
    private CompetitionRestService competitionRestService;
    private UserService userService;
    private SummaryViewModelFragmentPopulator summaryViewModelPopulator;
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;
    private ApplicationResearchCategorySummaryModelPopulator researchCategorySummaryModelPopulator;
    private ProjectService projectService;
    private GrantTransferSummaryPopulator grantTransferSummaryPopulator;

    public ApplicationSummaryViewModelPopulator(ApplicationService applicationService,
                                                ApplicationRestService applicationRestService,
                                                CompetitionRestService competitionRestService,
                                                UserService userService,
                                                SummaryViewModelFragmentPopulator summaryViewModelPopulator,
                                                ApplicationTeamModelPopulator applicationTeamModelPopulator,
                                                ApplicationResearchCategorySummaryModelPopulator researchCategorySummaryModelPopulator,
                                                ProjectService projectService,
                                                GrantTransferSummaryPopulator grantTransferSummaryPopulator) {
        this.applicationService = applicationService;
        this.applicationRestService = applicationRestService;
        this.competitionRestService = competitionRestService;
        this.userService = userService;
        this.summaryViewModelPopulator = summaryViewModelPopulator;
        this.applicationTeamModelPopulator = applicationTeamModelPopulator;
        this.researchCategorySummaryModelPopulator = researchCategorySummaryModelPopulator;
        this.projectService = projectService;
        this.grantTransferSummaryPopulator = grantTransferSummaryPopulator;
    }

    public ApplicationSummaryViewModel populate(ApplicationResource application, CompetitionResource competition, UserResource user, ApplicationForm form, boolean isSupport) {

        boolean userIsLeadApplicant = userService.isLeadApplicant(user.getId(), application);
        long applicationId = application.getId();

        ApplicationTeamViewModel applicationTeamViewModel = applicationTeamModelPopulator.populateSummaryModel(
                applicationId, user.getId(), application.getCompetition());

        ResearchCategorySummaryViewModel researchCategorySummaryViewModel =
                researchCategorySummaryModelPopulator.populate(application, user.getId(), userIsLeadApplicant);

        GrantTransferDetailsSummaryViewModel grantTransferDetailsSummaryViewModel = null;
        GrantAgreementSummaryViewModel grantAgreementSummaryViewModel = null;

        if (competition.isH2020()) {
            grantTransferDetailsSummaryViewModel = grantTransferSummaryPopulator.populateDetails(application);
            grantAgreementSummaryViewModel = grantTransferSummaryPopulator.populateAgreement(application);
        }

        return new ApplicationSummaryViewModel(
                application,
                competition,
                applicationRestService.isApplicationReadyForSubmit(applicationId).getSuccess(),
                summaryViewModelPopulator.populate(applicationId, user, form),
                applicationTeamViewModel,
                researchCategorySummaryViewModel,
                userService.isLeadApplicant(user.getId(), application),
                isProjectWithdrawn(applicationId),
                isSupport,
                application.isCollaborativeProject(),
                grantTransferDetailsSummaryViewModel,
                grantAgreementSummaryViewModel);
    }

    private boolean isProjectWithdrawn(Long applicationId) {
        ProjectResource project = projectService.getByApplicationId(applicationId);
        return project != null && project.isWithdrawn();
    }
}
