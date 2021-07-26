package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.status.populator.SetupSectionStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.sections.SectionStatus.*;

@Component
public class ProjectFilterPopulator {

    private final CompetitionRestService competitionRestService;
    private final SetupSectionStatus sectionStatus;

    public ProjectFilterPopulator(CompetitionRestService competitionRestService, SetupSectionStatus sectionStatus) {
        this.competitionRestService = competitionRestService;
        this.sectionStatus = sectionStatus;
    }

    public List<ProjectResource> getInSetupProjects(List<ProjectResource> projects) {
        return projects.stream()
                .filter(project -> !project.getProjectState().isComplete())
                .collect(Collectors.toList());

    }

    public List<ProjectResource> getPreviousProject(List<ProjectResource> projects) {
        return projects.stream()
                .filter(project -> project.getProjectState().isComplete())
                .collect(Collectors.toList());
    }

    public List<ProjectResource> getProjectsWithDocumentsComplete(List<ProjectResource> projects) {
        return projects.stream()
                .filter(project -> hasDocumentSection(project)
                        && sectionStatus.documentsSectionStatus(false, project, competitionRestService.getCompetitionForProject(project.getId()).getSuccess(), true).equals(TICK))
                .collect(Collectors.toList());
    }

    public List<ProjectResource> getProjectsWithDocumentsInComplete(List<ProjectResource> projects) {
        return projects.stream()
                .filter(project -> hasDocumentSection(project)
                        && sectionStatus.documentsSectionStatus(false, project, competitionRestService.getCompetitionForProject(project.getId()).getSuccess(), true).equals(INCOMPLETE))
                .collect(Collectors.toList());
    }

    public List<ProjectResource> getProjectsWithDocumentsAwaitingReview(List<ProjectResource> projects) {
        return projects.stream()
                .filter(project -> hasDocumentSection(project)
                        && sectionStatus.documentsSectionStatus(false, project, competitionRestService.getCompetitionForProject(project.getId()).getSuccess(), true).equals(MO_ACTION_REQUIRED))
                .collect(Collectors.toList());
    }

    public boolean hasDocumentSection(ProjectResource project) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        return competitionResource.getProjectSetupStages().contains(ProjectSetupStage.DOCUMENTS);
    }
}
