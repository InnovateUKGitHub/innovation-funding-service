package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.yourorganisation.viewmodel.ProjectYourOrganisationViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.SUBSIDY_BASIS;

/**
 * A populator to build a YourOrganisationViewModel for the "Your organisation" pages.
 */
@Component
public class YourOrganisationViewModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Autowired
    private QuestionRestService questionRestService;

    public ProjectYourOrganisationViewModel populate(long projectId, long organisationId, UserResource user) {
        PendingPartnerProgressResource progress = pendingPartnerProgressRestService.getPendingPartnerProgress(projectId, organisationId).getSuccess();
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        PendingPartnerProgressResource pendingPartner = pendingPartnerProgressRestService.getPendingPartnerProgress(projectId, organisationId).getSuccess();

        boolean isMaximumFundingLevelConstant = competition.isMaximumFundingLevelConstant(
                organisation::getOrganisationTypeEnum,
                () -> grantClaimMaximumRestService.isMaximumFundingLevelConstant(competition.getId()).getSuccess());

        Optional<Long> subsidyQuestionId = progress.isSubsidyBasisRequired()
                ? Optional.of(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), SUBSIDY_BASIS).getSuccess().getId())
                : Optional.empty();

        boolean showOrganisationSizeAlert = !isMaximumFundingLevelConstant && pendingPartner.isYourFundingComplete();
        return new ProjectYourOrganisationViewModel(
                project.getApplication(),
                competition,
                organisation,
                isMaximumFundingLevelConstant,
                showOrganisationSizeAlert,
                projectId,
                project.getName(),
                pendingPartner.isYourOrganisationComplete(),
                user,
                true,
                progress.isSubsidyBasisRequiredAndNotCompleted(),
                subsidyQuestionId);
    }
}