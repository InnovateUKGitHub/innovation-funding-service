package org.innovateuk.ifs.documents.populator;

import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.documents.viewModel.AllDocumentsViewModel;
import org.innovateuk.ifs.documents.viewModel.DocumentViewModel;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentResource;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentStatus;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindAny;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Component
public class DocumentsPopulator {

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ProjectRestService projectRestService;

    public AllDocumentsViewModel populateAllDocuments(long projectId, long loggedInUserId) {

        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();

        List<CompetitionDocumentResource> configuredProjectDocuments = getCompetitionDocuments(project.getCompetition());

        List<PartnerOrganisationResource> partnerOrganisations =
                partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId()).getSuccess();

        if (partnerOrganisations.size() == 1) {
            configuredProjectDocuments.removeIf(
                    document -> document.getTitle().equals(COLLABORATION_AGREEMENT_TITLE));
        }

        List<ProjectDocumentResource> projectDocuments = project.getProjectDocuments();

        List<ProjectDocumentStatus> documents = simpleMap(configuredProjectDocuments, configuredDocument ->
                new ProjectDocumentStatus(configuredDocument.getId(), configuredDocument.getTitle(),
                        getProjectDocumentStatus(projectDocuments, configuredDocument.getId())));

        return new AllDocumentsViewModel(project, documents, isProjectManager(loggedInUserId, projectId));
    }

    private DocumentStatus getProjectDocumentStatus(List<ProjectDocumentResource> projectDocuments, Long documentConfigId) {

        return simpleFindAny(projectDocuments, projectDocumentResource -> projectDocumentResource.getCompetitionDocument().getId().equals(documentConfigId))
                .map(ProjectDocumentResource::getStatus)
                .orElse(DocumentStatus.UNSET);
    }

    public DocumentViewModel populateViewDocument(long projectId, long loggedInUserId, long documentConfigId) {

        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();

        List<CompetitionDocumentResource> configuredProjectDocuments = getCompetitionDocuments(project.getCompetition());

        CompetitionDocumentResource configuredProjectDocument =
                simpleFindAny(configuredProjectDocuments,
                        projectDocumentResource -> projectDocumentResource.getId().equals(documentConfigId))
                        .get();

        Optional<ProjectDocumentResource> projectDocument = simpleFindAny(project.getProjectDocuments(),
                projectDocumentResource -> projectDocumentResource.getCompetitionDocument().getId().equals(documentConfigId));

        FileDetailsViewModel fileDetails = projectDocument.map(ProjectDocumentResource::getFileEntry)
                .map(FileDetailsViewModel::new)
                .orElse(null);

        return new DocumentViewModel(project.getId(),
                project.getName(),
                project.getApplication(),
                configuredProjectDocument.getId(),
                configuredProjectDocument.getTitle(),
                configuredProjectDocument.getGuidance(),
                fileDetails,
                projectDocument.map(ProjectDocumentResource::getStatus).orElse(DocumentStatus.UNSET),
                projectDocument.map(ProjectDocumentResource::getStatusComments).orElse(""),
                isProjectManager(loggedInUserId, projectId),
                project.getProjectState().isActive());
    }

    private boolean isProjectManager(long loggedInUserId, long projectId) {
        return Optional.ofNullable(projectRestService.getProjectManager(projectId).getOptionalSuccessObject())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ProjectUserResource::getUser)
                .map(userId -> userId.equals(loggedInUserId))
                .orElse(false);
    }

    private List<CompetitionDocumentResource> getCompetitionDocuments(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId)
                .andOnSuccessReturn(CompetitionResource::getCompetitionDocuments)
                .getSuccess();
    }
}
