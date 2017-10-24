package org.innovateuk.ifs.project.otherdocuments.populator;

import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.otherdocuments.OtherDocumentsService;
import org.innovateuk.ifs.project.otherdocuments.viewmodel.OtherDocumentsViewModel;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Component
public class OtherDocumentsViewModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OtherDocumentsService otherDocumentsService;

    public OtherDocumentsViewModel populate(Long projectId, UserResource loggedInUser) {

    List<OrganisationResource> partnerOrganisations = projectService.getPartnerOrganisationsForProject(projectId);
    boolean collaborationAgreementRequired = partnerOrganisations.size() > 1;

    ProjectResource project = projectService.getById(projectId);
    Optional<FileEntryResource> collaborationAgreement = null;
    if (collaborationAgreementRequired) {
        collaborationAgreement = otherDocumentsService.getCollaborationAgreementFileDetails(projectId);
    }
    Optional<FileEntryResource> exploitationPlan = otherDocumentsService.getExploitationPlanFileDetails(projectId);

    List<String> partnerOrganisationNames = simpleMap(partnerOrganisations, OrganisationResource::getName);
    boolean isProjectManager = projectService.isProjectManager(loggedInUser.getId(), projectId);
    boolean isSubmitAllowed = otherDocumentsService.isOtherDocumentSubmitAllowed(projectId);
    List<String> rejectionReasons = emptyList();

    boolean otherDocumentsSubmitted = project.getDocumentsSubmittedDate() != null;
    ApprovalType otherDocumentsApproved = project.getOtherDocumentsApproved();

    return new OtherDocumentsViewModel(projectId, project.getApplication(), project.getName(),
            collaborationAgreementRequired ? collaborationAgreement.map(FileDetailsViewModel::new).orElse(null) : null,
            exploitationPlan.map(FileDetailsViewModel::new).orElse(null),
            partnerOrganisationNames, rejectionReasons,
            isProjectManager, otherDocumentsSubmitted, otherDocumentsApproved,
            isSubmitAllowed,
            project.getDocumentsSubmittedDate(),
            collaborationAgreementRequired);
    }
}
