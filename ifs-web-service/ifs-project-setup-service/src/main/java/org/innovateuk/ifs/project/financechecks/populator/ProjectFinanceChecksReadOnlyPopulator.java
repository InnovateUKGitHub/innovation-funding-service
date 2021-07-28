package org.innovateuk.ifs.project.financechecks.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksReadOnlyViewModel;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectOrganisationRowViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class ProjectFinanceChecksReadOnlyPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public ProjectFinanceChecksReadOnlyViewModel populate(long projectId) {

        ProjectResource project = projectService.getById(projectId);
        List<OrganisationResource> projectOrganisations = projectService.getPartnerOrganisationsForProject(projectId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();

        List<ProjectOrganisationRowViewModel> projectOrganisationRows = projectOrganisations.stream()
                .map(org -> new ProjectOrganisationRowViewModel(
                        org.getId(),
                        org.getName(),
                        org.equals(leadOrganisation)))
                .sorted()
                .collect(toList());

        return new ProjectFinanceChecksReadOnlyViewModel(project.getId(), project.getName(),
                competition.isProcurementMilestones(), projectOrganisationRows);
    }
}
