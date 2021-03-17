package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.ProjectTermsViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.SUBSIDY_BASIS;

@Component
public class ProjectTermsModelPopulator {
    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Autowired
    private QuestionRestService questionRestService;

    public ProjectTermsViewModel populate(long projectId,
                                          long organisationId) {
        PendingPartnerProgressResource progress = pendingPartnerProgressRestService.getPendingPartnerProgress(projectId, organisationId).getSuccess();
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        PendingPartnerProgressResource pendingPartnerProgressResource = pendingPartnerProgressRestService.getPendingPartnerProgress(projectId, organisationId).getSuccess();
        boolean subsidyBasisRequired = competition.isSubsidyControl();
        Optional<Long> subsidyQuestionId = subsidyBasisRequired
                ? Optional.of(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), SUBSIDY_BASIS).getSuccess().getId())
                : Optional.empty();

        return new ProjectTermsViewModel(
                projectId,
                organisation.getId(),
                competition.getTermsAndConditions().getTemplate(),
                pendingPartnerProgressResource.isTermsAndConditionsComplete(),
                pendingPartnerProgressResource.getTermsAndConditionsCompletedOn(),
                subsidyBasisRequired && !progress.isSubsidyBasisComplete(),
                subsidyQuestionId
        );
    }
}
