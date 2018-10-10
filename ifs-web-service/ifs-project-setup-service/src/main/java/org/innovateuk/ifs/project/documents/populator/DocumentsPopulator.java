package org.innovateuk.ifs.project.documents.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.core.populator.BasicDetailsPopulator;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentResource;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentStatus;
import org.innovateuk.ifs.project.documents.viewmodel.AllDocumentsViewModel;
import org.innovateuk.ifs.project.documents.viewmodel.DocumentViewModel;
import org.innovateuk.ifs.project.resource.BasicDetails;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindAny;

@Component
public class DocumentsPopulator {

    @Autowired
    private BasicDetailsPopulator basicDetailsPopulator;

    @Autowired
    private ProjectService projectService;

    public AllDocumentsViewModel populateAllDocuments(long projectId) {

        BasicDetails basicDetails = basicDetailsPopulator.populate(projectId);
        ProjectResource project = basicDetails.getProject();
        CompetitionResource competition = basicDetails.getCompetition();

        List<org.innovateuk.ifs.competition.resource.ProjectDocumentResource> configuredProjectDocuments = competition.getProjectDocuments();
        List<ProjectDocumentResource> projectDocuments = project.getProjectDocuments();

        List<ProjectDocumentStatus> documents = new ArrayList<>();

        for (org.innovateuk.ifs.competition.resource.ProjectDocumentResource configuredDocument : configuredProjectDocuments) {
            documents.add(new ProjectDocumentStatus(configuredDocument.getId(),
                    configuredDocument.getTitle(),
                    getProjectDocumentStatus(projectDocuments, configuredDocument.getId())));
        }

        return new AllDocumentsViewModel(projectId, project.getName(), documents);
    }

    private DocumentStatus getProjectDocumentStatus(List<ProjectDocumentResource> projectDocuments, Long documentConfigId) {

        return simpleFindAny(projectDocuments, projectDocumentResource -> projectDocumentResource.getProjectDocument().getId().equals(documentConfigId))
                .map(projectDocumentResource -> projectDocumentResource.getStatus())
                .orElse(DocumentStatus.UNSET);
    }

    public DocumentViewModel populateViewDocument(long projectId, long documentConfigId, UserResource loggedInUser) {

        BasicDetails basicDetails = basicDetailsPopulator.populate(projectId);
        ProjectResource project = basicDetails.getProject();
        CompetitionResource competition = basicDetails.getCompetition();

        boolean isProjectManager = projectService.isProjectManager(loggedInUser.getId(), projectId);

        org.innovateuk.ifs.competition.resource.ProjectDocumentResource configuredProjectDocument =
                simpleFindAny(competition.getProjectDocuments(),
                             projectDocumentResource -> projectDocumentResource.getId().equals(documentConfigId))
                .get();

        Optional<ProjectDocumentResource> projectDocument = simpleFindAny(project.getProjectDocuments(),
                    projectDocumentResource -> projectDocumentResource.getProjectDocument().getId().equals(documentConfigId));

        FileDetailsViewModel fileDetails = projectDocument.map(projectDocumentResource -> projectDocumentResource.getFileEntry())
                .map(FileDetailsViewModel::new)
                .orElse(null);

        return new DocumentViewModel(project.getId(), project.getName(),
                configuredProjectDocument.getId(), configuredProjectDocument.getTitle(), configuredProjectDocument.getGuidance(),
                fileDetails,
                projectDocument.map(projectDocumentResource -> projectDocumentResource.getStatus()).orElse(DocumentStatus.UNSET),
                isProjectManager);
    }
}
