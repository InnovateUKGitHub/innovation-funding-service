package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.grantofferletter.GrantOfferLetterService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.GrantOfferLetterModel;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GrantOfferLetterModelPopulator {

    @Autowired
    public ProjectService projectService;

    @Autowired
    public GrantOfferLetterService grantOfferLetterService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private MonitoringOfficerRestService monitoringOfficerRestService;

    public GrantOfferLetterModel populateGrantOfferLetterViewModel(Long projectId, UserResource loggedInUser) {
        ProjectResource project = projectService.getById(projectId);
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();

        Optional<FileEntryResource> signedGrantOfferLetterFile = grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId);
        Optional<FileEntryResource> grantOfferFileDetails = grantOfferLetterService.getGrantOfferFileDetails(projectId);
        Optional<FileEntryResource> additionalContractFile = grantOfferLetterService.getAdditionalContractFileDetails(projectId);
        Optional<FileEntryResource> signedAdditionalContractFile = grantOfferLetterService.getSignedAdditionalContractFileDetails(projectId);
        boolean leadPartner = projectService.isUserLeadPartner(projectId, loggedInUser.getId());
        boolean projectManager = projectService.isProjectManager(loggedInUser.getId(), projectId);
        boolean financeContact = projectService.isProjectFinanceContact(loggedInUser.getId(), projectId);
        GrantOfferLetterStateResource state = grantOfferLetterService.getGrantOfferLetterState(projectId).getSuccess();
        boolean monitoringOfficer = monitoringOfficerRestService.isMonitoringOfficerOnProject(projectId, loggedInUser.getId()).getSuccess();

        return new GrantOfferLetterModel(
                competition.isProcurement() ? "Contract" : "Grant offer letter",
                projectId, project.getName(),
                leadPartner,
                grantOfferFileDetails.map(FileDetailsViewModel::new).orElse(null),
                signedGrantOfferLetterFile.map(FileDetailsViewModel::new).orElse(null),
                additionalContractFile.map(FileDetailsViewModel::new).orElse(null),
                signedAdditionalContractFile.map(FileDetailsViewModel::new).orElse(null),
                projectManager,
                financeContact,
                state,
                project.isUseDocusignForGrantOfferLetter(),
                competition.isProcurement(),
                competition.isKtp(),
                monitoringOfficer);
    }
}
