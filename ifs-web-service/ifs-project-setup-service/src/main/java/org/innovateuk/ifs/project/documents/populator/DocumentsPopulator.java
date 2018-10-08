package org.innovateuk.ifs.project.documents.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentStatus;
import org.innovateuk.ifs.project.documents.viewmodel.AllDocumentsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentsPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public AllDocumentsViewModel populate(long projectId) {

        ProjectResource project = projectService.getById(projectId);

        ApplicationResource application = applicationService.getById(project.getApplication());

        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        List<ProjectDocumentResource> configuredProjectDocuments = competition.getProjectDocuments();
        List<org.innovateuk.ifs.project.document.resource.ProjectDocumentResource> projectDocuments = project.getProjectDocuments();

        List<ProjectDocumentStatus> documents = new ArrayList<>();

        for (ProjectDocumentResource configuredDocument : configuredProjectDocuments) {
            documents.add(new ProjectDocumentStatus(configuredDocument.getId(),
                    configuredDocument.getTitle(),
                    getProjectDocumentStatus(projectDocuments, configuredDocument.getId())));
        }

        return new AllDocumentsViewModel(projectId, project.getName(), documents);
    }

    private DocumentStatus getProjectDocumentStatus(List<org.innovateuk.ifs.project.document.resource.ProjectDocumentResource> projectDocuments, Long documentConfigId) {

        return projectDocuments.stream().filter(projectDocumentResource -> projectDocumentResource.getProjectDocument().getId().equals(documentConfigId))
                .findFirst()
                .map(projectDocumentResource -> projectDocumentResource.getStatus())
                .orElse(DocumentStatus.UNSET);
    }
}
