package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.grantofferletter.ProjectGrantOfferService;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProjectGrantOfferLetterViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.function.Function.identity;

@Service
public class ProjectGrantOfferLetterViewModelPopulator {

    @Autowired
    public ProjectService projectService;

    @Autowired
    public ProjectGrantOfferService projectGrantOfferService;

    public ProjectGrantOfferLetterViewModel populateGrantOfferLetterViewModel(Long projectId, UserResource loggedInUser) {
        ProjectResource project = projectService.getById(projectId);
        boolean leadPartner = projectService.isUserLeadPartner(projectId, loggedInUser.getId());

        Optional<FileEntryResource> signedGrantOfferLetterFile = projectGrantOfferService.getSignedGrantOfferLetterFileDetails(projectId);
        Optional<FileEntryResource> grantOfferFileDetails = projectGrantOfferService.getGrantOfferFileDetails(projectId);
        Optional<FileEntryResource> additionalContractFile = projectGrantOfferService.getAdditionalContractFileDetails(projectId);
        Boolean grantOfferLetterApproved = projectGrantOfferService.isSignedGrantOfferLetterApproved(projectId).getSuccessObject();
        boolean isProjectManager = projectService.isProjectManager(loggedInUser.getId(), projectId);
        boolean isGrantOfferLetterSent = projectGrantOfferService.isGrantOfferLetterAlreadySent(projectId).getOptionalSuccessObject().map(identity()).orElse(false);

        return new ProjectGrantOfferLetterViewModel(projectId, project.getName(),
                leadPartner,
                grantOfferFileDetails.isPresent() ? grantOfferFileDetails.map(FileDetailsViewModel::new).orElse(null) : null,
                signedGrantOfferLetterFile.isPresent() ? signedGrantOfferLetterFile.map(FileDetailsViewModel::new).orElse(null) : null,
                additionalContractFile.isPresent() ? additionalContractFile.map(FileDetailsViewModel::new).orElse(null) : null,
                project.getOfferSubmittedDate(),
                grantOfferLetterApproved,
                isProjectManager,
                isGrantOfferLetterSent);
    }

}
