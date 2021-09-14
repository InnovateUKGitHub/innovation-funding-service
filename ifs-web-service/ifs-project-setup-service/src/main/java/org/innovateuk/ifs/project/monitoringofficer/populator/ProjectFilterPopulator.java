package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.spendprofile.service.SpendProfileRestService;
import org.innovateuk.ifs.project.status.populator.SetupSectionStatus;
import org.innovateuk.ifs.sections.SectionStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.project.internal.ProjectSetupStage.DOCUMENTS;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.SPEND_PROFILE;
import static org.innovateuk.ifs.sections.SectionStatus.*;

@Component
public class ProjectFilterPopulator {

    private final CompetitionRestService competitionRestService;
    private final SetupSectionStatus sectionStatus;
    private final SpendProfileRestService spendProfileRestService;

    public ProjectFilterPopulator(CompetitionRestService competitionRestService, SetupSectionStatus sectionStatus, SpendProfileRestService spendProfileRestService) {
        this.competitionRestService = competitionRestService;
        this.sectionStatus = sectionStatus;
        this.spendProfileRestService = spendProfileRestService;
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
                .filter(project -> sectionStatus.documentsSectionStatus(false, project, competitionRestService.getCompetitionForProject(project.getId()).getSuccess(), true).equals(TICK))
                .collect(Collectors.toList());
    }

    public List<ProjectResource> getProjectsWithDocumentsInComplete(List<ProjectResource> projects) {
        return projects.stream()
                .filter(project -> sectionStatus.documentsSectionStatus(false, project, competitionRestService.getCompetitionForProject(project.getId()).getSuccess(), true).equals(INCOMPLETE))
                .collect(Collectors.toList());
    }

    public List<ProjectResource> getProjectsWithDocumentsAwaitingReview(List<ProjectResource> projects) {
        return projects.stream()
                .filter(project -> sectionStatus.documentsSectionStatus(false, project, competitionRestService.getCompetitionForProject(project.getId()).getSuccess(), true).equals(MO_ACTION_REQUIRED))
                .collect(Collectors.toList());
    }

    public boolean hasDocumentSection(ProjectResource project) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        return competitionResource.getProjectSetupStages().contains(DOCUMENTS);
    }

    public boolean hasSpendProfileSection(ProjectResource project) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        return competitionResource.getProjectSetupStages().contains(SPEND_PROFILE);

    }

    public List<ProjectResource> getProjectsWithSpendProfileComplete(List<ProjectResource> projects) {
        return projects.stream()
                .filter(project -> getSpendProfileSectionStatus(project).equals(TICK))
                .collect(Collectors.toList());
    }

    public List<ProjectResource> getProjectsWithSpendProfileInComplete(List<ProjectResource> projects) {
        return projects.stream()
                .filter(project -> getSpendProfileSectionStatus(project).equals(INCOMPLETE))
                .collect(Collectors.toList());
    }

    public List<ProjectResource> getProjectsWithSpendProfileAwaitingReview(List<ProjectResource> projects) {
        return projects.stream()
                .filter(project -> getSpendProfileSectionStatus(project).equals(MO_ACTION_REQUIRED))
                .collect(Collectors.toList());
    }

    public SectionStatus getSpendProfileSectionStatus(ProjectResource project) {
        boolean allOrganisationsSpendProfileSubmitted = project.getSpendProfileSubmittedDate() != null;
        boolean projectSpendProfileHasBeenApproved = spendProfileRestService.getSpendProfileStatusByProjectId(project.getId()).getSuccess().equals(ApprovalType.APPROVED);

        if (allOrganisationsSpendProfileSubmitted && projectSpendProfileHasBeenApproved) {
            return TICK;
        }
        else if (!allOrganisationsSpendProfileSubmitted) {
            return INCOMPLETE;
        }
        else return MO_ACTION_REQUIRED;
    }
}
