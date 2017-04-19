package org.innovateuk.ifs.project.otherdocuments.populator;

import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.otherdocuments.viewmodel.ProjectOtherDocumentsViewModel;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Service
public class ProjectOtherDocumentsViewModelPopulator {

      public ProjectOtherDocumentsViewModel getOtherDocumentsViewModel(Long projectId, UserResource loggedInUser, ProjectService projectService) {

        ProjectResource project = projectService.getById(projectId);
        Optional<FileEntryResource> collaborationAgreement = projectService.getCollaborationAgreementFileDetails(projectId);
        Optional<FileEntryResource> exploitationPlan = projectService.getExploitationPlanFileDetails(projectId);
        List<OrganisationResource> partnerOrganisations = projectService.getPartnerOrganisationsForProject(projectId);

        List<String> partnerOrganisationNames = simpleMap(partnerOrganisations, OrganisationResource::getName);
        boolean isProjectManager = projectService.isProjectManager(loggedInUser.getId(), projectId);
        boolean isSubmitAllowed = projectService.isOtherDocumentSubmitAllowed(projectId);
        List<String> rejectionReasons = emptyList();

        boolean otherDocumentsSubmitted = project.getDocumentsSubmittedDate() != null;
        ApprovalType otherDocumentsApproved = project.getOtherDocumentsApproved();

        return new ProjectOtherDocumentsViewModel(projectId, project.getApplication(), project.getName(),
                collaborationAgreement.map(FileDetailsViewModel::new).orElse(null),
                exploitationPlan.map(FileDetailsViewModel::new).orElse(null),
                partnerOrganisationNames, rejectionReasons,
                isProjectManager, otherDocumentsSubmitted, otherDocumentsApproved,
                isSubmitAllowed, project.getDocumentsSubmittedDate());
    }
}
