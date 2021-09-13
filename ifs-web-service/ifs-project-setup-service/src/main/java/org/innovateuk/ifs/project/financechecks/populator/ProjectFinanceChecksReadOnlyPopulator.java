package org.innovateuk.ifs.project.financechecks.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.eligibility.populator.ProjectFinanceChangesViewModelPopulator;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckPartnerStatusResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksReadOnlyViewModel;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectOrganisationRowViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
public class ProjectFinanceChecksReadOnlyPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private ProjectFinanceChangesViewModelPopulator projectFinanceChangesViewModelPopulator;

    public ProjectFinanceChecksReadOnlyViewModel populate(long projectId) {

        ProjectResource project = projectService.getById(projectId);
        List<OrganisationResource> projectOrganisations = projectService.getPartnerOrganisationsForProject(projectId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        FinanceCheckSummaryResource financeCheckSummaryResource = financeCheckService.getFinanceCheckSummary(projectId).getSuccess();

        Map<Long, Boolean> organisationStatuses = getOrganisationStatus(competition, financeCheckSummaryResource);

        List<ProjectOrganisationRowViewModel> projectOrganisationRows = projectOrganisations.stream()
                .map(org -> new ProjectOrganisationRowViewModel(
                        org.getId(),
                        org.getName(),
                        org.equals(leadOrganisation),
                        competition.isProcurementMilestones(),
                        projectFinanceChangesViewModelPopulator.getProjectFinanceChangesViewModel(false, project, org).hasChanges(),
                        organisationStatuses.get(org.getId())))
                .sorted(Comparator.comparingLong(ProjectOrganisationRowViewModel::getOrganisationId))
                .collect(toList());

        return new ProjectFinanceChecksReadOnlyViewModel(project.getId(), project.getName(), projectOrganisationRows);
    }

    private Map<Long, Boolean> getOrganisationStatus(CompetitionResource competitionResource, FinanceCheckSummaryResource financeCheckSummaryResource) {
        if (competitionResource.isProcurement()) {
            return financeCheckSummaryResource.getPartnerStatusResources()
                    .stream()
                    .collect(Collectors.toMap(
                            FinanceCheckPartnerStatusResource::getId,
                            FinanceCheckPartnerStatusResource::isFinanceChecksApprovedProcurement));
        }

        return financeCheckSummaryResource.getPartnerStatusResources()
                .stream()
                .collect(Collectors.toMap(
                        FinanceCheckPartnerStatusResource::getId,
                        FinanceCheckPartnerStatusResource::isFinanceChecksApprovedNonProcurement));
    }
}
