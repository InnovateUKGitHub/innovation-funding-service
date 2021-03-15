package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.ProjectYourFundingViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.SUBSIDY_BASIS;

@Component
public class ProjectYourFundingViewModelPopulator {

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private QuestionRestService questionRestService;

    public ProjectYourFundingViewModel populate(long projectId, long organisationId) {
        PendingPartnerProgressResource progress = pendingPartnerProgressRestService.getPendingPartnerProgress(projectId, organisationId).getSuccess();
        ProjectFinanceResource projectFinance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        boolean organisationSectionRequired = !competition.applicantShouldUseJesFinances(organisation.getOrganisationTypeEnum());
        boolean organisationRequiredAndNotCompleted = organisationSectionRequired && !progress.isYourOrganisationComplete();
        boolean fundingLevelConstant = grantClaimMaximumRestService.isMaximumFundingLevelConstant(competition.getId()).getSuccess();
        PartnerOrganisationResource partnerOrganisationResource = partnerOrganisationRestService.getPartnerOrganisation(projectId, organisationId).getSuccess();

        boolean subsidyBasisRequiredAndNotCompleted = progress.isSubsidyBasisRequired() && !progress.isSubsidyBasisComplete();
        Optional<Long> subsidyQuestionId = progress.isSubsidyBasisRequired()
                ? Optional.of(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), SUBSIDY_BASIS).getSuccess().getId())
                : Optional.empty();

        return new ProjectYourFundingViewModel(project,
                organisationId,
                progress.isYourFundingComplete(),
                projectFinance.getMaximumFundingLevel(),
                competition.getId(),
                fundingLevelConstant,
                competition.getFundingType(),
                organisation.getOrganisationTypeEnum(),
                partnerOrganisationResource.isLeadOrganisation(),
                subsidyBasisRequiredAndNotCompleted,
                organisationRequiredAndNotCompleted,
                subsidyQuestionId);
    }
}
