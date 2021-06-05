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
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;
import static org.innovateuk.ifs.user.resource.Authority.SUPER_ADMIN_USER;
import static org.innovateuk.ifs.user.resource.Role.*;
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

    @Autowired
    private MonitoringOfficerRestService monitoringOfficerRestService;

    @Autowired
    private UserRestService userRestService;

    @Value("${ifs.monitoringofficer.journey.update.enabled}")
    private boolean isMOJourneyUpdateEnabled;

    public AllDocumentsViewModel populateAllDocuments(long projectId, long loggedInUserId) {

        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();

        CompetitionResource competition = getCompetition(project.getCompetition());
        List<CompetitionDocumentResource> configuredProjectDocuments = competition.getCompetitionDocuments();

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

        boolean isMOCanApproveOrRejectDocuments = isMOJourneyUpdateEnabled && isMonitoringOfficer(loggedInUserId, projectId);

        return new AllDocumentsViewModel(project, documents, isProjectManager(loggedInUserId, projectId), competition.isProcurement(), isMOCanApproveOrRejectDocuments);
    }

    private DocumentStatus getProjectDocumentStatus(List<ProjectDocumentResource> projectDocuments, Long documentConfigId) {

        return simpleFindAny(projectDocuments, projectDocumentResource -> projectDocumentResource.getCompetitionDocument().getId().equals(documentConfigId))
                .map(ProjectDocumentResource::getStatus)
                .orElse(DocumentStatus.UNSET);
    }

    public DocumentViewModel populateViewDocument(long projectId, UserResource loggedInUser, long documentConfigId) {

        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();

        List<CompetitionDocumentResource> configuredProjectDocuments = getCompetition(project.getCompetition()).getCompetitionDocuments();

        CompetitionDocumentResource configuredProjectDocument =
                simpleFindAny(configuredProjectDocuments,
                        projectDocumentResource -> projectDocumentResource.getId().equals(documentConfigId))
                        .get();

        Optional<ProjectDocumentResource> projectDocument = simpleFindAny(project.getProjectDocuments(),
                projectDocumentResource -> projectDocumentResource.getCompetitionDocument().getId().equals(documentConfigId));

        FileDetailsViewModel fileDetails = projectDocument.map(ProjectDocumentResource::getFileEntry)
                .map(FileDetailsViewModel::new)
                .orElse(null);

        // if isMOJourneyUpdateEnabled toggle is set to false, IFSAdmin CompAdmin and Finance user can approve (excluding MO). If set to True, only IFSAdmin can approve.
        boolean userCanApproveOrRejectDocuments = !isMOJourneyUpdateEnabled ? loggedInUser.hasAnyRoles(COMP_ADMIN, PROJECT_FINANCE, IFS_ADMINISTRATOR) : loggedInUser.hasAnyRoles(IFS_ADMINISTRATOR, MONITORING_OFFICER);

        return new DocumentViewModel(project.getId(),
                project.getName(),
                project.getApplication(),
                configuredProjectDocument.getId(),
                configuredProjectDocument.getTitle(),
                configuredProjectDocument.getGuidance(),
                fileDetails,
                projectDocument.map(ProjectDocumentResource::getStatus).orElse(DocumentStatus.UNSET),
                projectDocument.map(ProjectDocumentResource::getStatusComments).orElse(""),
                isProjectManager(loggedInUser.getId(), projectId),
                project.getProjectState().isActive(),
                loggedInUser.hasAuthority(SUPER_ADMIN_USER),
                userCanApproveOrRejectDocuments);
    }

    private boolean isProjectManager(long loggedInUserId, long projectId) {
        return Optional.ofNullable(projectRestService.getProjectManager(projectId).getOptionalSuccessObject())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ProjectUserResource::getUser)
                .map(userId -> userId.equals(loggedInUserId))
                .orElse(false);
    }

    private boolean isMonitoringOfficer(long loggedInUserId, long projectId) {
        return monitoringOfficerRestService.isMonitoringOfficerOnProject(projectId, loggedInUserId).getSuccess();
    }

    private CompetitionResource getCompetition(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).getSuccess();
    }
}
