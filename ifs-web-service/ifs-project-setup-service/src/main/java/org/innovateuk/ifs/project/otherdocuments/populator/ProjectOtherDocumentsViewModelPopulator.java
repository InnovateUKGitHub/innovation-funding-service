package org.innovateuk.ifs.project.otherdocuments.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.monitoringofficer.ProjectMonitoringOfficerService;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.otherdocuments.ProjectOtherDocumentsService;
import org.innovateuk.ifs.project.otherdocuments.viewmodel.ProjectOtherDocumentsViewModel;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.sections.ProjectSetupSectionAccessibilityHelper;
import org.innovateuk.ifs.project.sections.ProjectSetupSectionStatus;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.project.sections.SectionStatus;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.project.sections.SectionStatus.TICK;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Component
public class ProjectOtherDocumentsViewModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectOtherDocumentsService projectOtherDocumentsService;

    @Autowired
    private ProjectMonitoringOfficerService projectMonitoringOfficerService;

    public ProjectOtherDocumentsViewModel populate(Long projectId, UserResource loggedInUser) {

    ProjectResource project = projectService.getById(projectId);
    Optional<FileEntryResource> collaborationAgreement = projectOtherDocumentsService.getCollaborationAgreementFileDetails(projectId);
    Optional<FileEntryResource> exploitationPlan = projectOtherDocumentsService.getExploitationPlanFileDetails(projectId);
    List<OrganisationResource> partnerOrganisations = projectService.getPartnerOrganisationsForProject(projectId);

    List<String> partnerOrganisationNames = simpleMap(partnerOrganisations, OrganisationResource::getName);
    boolean isProjectManager = projectService.isProjectManager(loggedInUser.getId(), projectId);
    boolean isSubmitAllowed = projectOtherDocumentsService.isOtherDocumentSubmitAllowed(projectId);
    List<String> rejectionReasons = emptyList();

    boolean otherDocumentsSubmitted = project.getDocumentsSubmittedDate() != null;
    ApprovalType otherDocumentsApproved = project.getOtherDocumentsApproved();
    //boolean projectComplete = isProjectComplete(projectId, loggedInUser);

    return new ProjectOtherDocumentsViewModel(projectId, project.getApplication(), project.getName(),
            collaborationAgreement.map(FileDetailsViewModel::new).orElse(null),
            exploitationPlan.map(FileDetailsViewModel::new).orElse(null),
            partnerOrganisationNames, rejectionReasons,
            isProjectManager, otherDocumentsSubmitted, otherDocumentsApproved,
            isSubmitAllowed, project.getDocumentsSubmittedDate());
    }

    private boolean isProjectComplete(Long projectId, UserResource loggedInUser) {
        Optional<MonitoringOfficerResource> monitoringOfficer = projectMonitoringOfficerService.getMonitoringOfficerForProject(projectId);
        OrganisationResource organisation = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        ProjectTeamStatusResource teamStatus = projectService.getProjectTeamStatus(projectId, Optional.empty());
        ProjectPartnerStatusResource ownOrganisation = teamStatus.getPartnerStatusForOrganisation(organisation.getId()).get();
        ProjectSetupSectionAccessibilityHelper statusAccessor = new ProjectSetupSectionAccessibilityHelper(teamStatus);
        ProjectSetupSectionStatus sectionStatus = new ProjectSetupSectionStatus();

        boolean isLeadPartner = teamStatus.getLeadPartnerStatus().getOrganisationId().equals(organisation.getId());
        boolean isProjectDetailsSubmitted = COMPLETE.equals(teamStatus.getLeadPartnerStatus().getProjectDetailsStatus());
        boolean isProjectDetailsProcessCompleted = isLeadPartner ? checkLeadPartnerProjectDetailsProcessCompleted(teamStatus) : statusAccessor.isFinanceContactSubmitted(organisation);
        boolean awaitingProjectDetailsActionFromOtherPartners = isLeadPartner && awaitingProjectDetailsActionFromOtherPartners(teamStatus);

        SectionAccess financeChecksAccess = statusAccessor.canAccessFinanceChecksSection(organisation);
        SectionStatus projectDetailsStatus = sectionStatus.projectDetailsSectionStatus(isProjectDetailsProcessCompleted, awaitingProjectDetailsActionFromOtherPartners, isLeadPartner);
        SectionStatus monitoringOfficerStatus = sectionStatus.monitoringOfficerSectionStatus(monitoringOfficer.isPresent(), isProjectDetailsSubmitted);
        SectionStatus financeChecksStatus = sectionStatus.financeChecksSectionStatus(ownOrganisation.getFinanceChecksStatus(), financeChecksAccess);
        SectionStatus spendProfileStatus= sectionStatus.spendProfileSectionStatus(ownOrganisation.getSpendProfileStatus());
        SectionStatus grantOfferStatus = sectionStatus.grantOfferLetterSectionStatus(ownOrganisation.getGrantOfferLetterStatus(), isLeadPartner);

        return projectDetailsStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus())
                && monitoringOfficerStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus())
                && financeChecksStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus())
                && spendProfileStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus())
                && grantOfferStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus());
    }

    private boolean checkLeadPartnerProjectDetailsProcessCompleted(ProjectTeamStatusResource teamStatus) {

        ProjectPartnerStatusResource leadPartnerStatus = teamStatus.getLeadPartnerStatus();

        return COMPLETE.equals(leadPartnerStatus.getProjectDetailsStatus())
                && COMPLETE.equals(leadPartnerStatus.getFinanceContactStatus())
                && allOtherPartnersFinanceContactStatusComplete(teamStatus);
    }

    private boolean awaitingProjectDetailsActionFromOtherPartners(ProjectTeamStatusResource teamStatus) {

        ProjectPartnerStatusResource leadPartnerStatus = teamStatus.getLeadPartnerStatus();

        return COMPLETE.equals(leadPartnerStatus.getProjectDetailsStatus())
                && COMPLETE.equals(leadPartnerStatus.getFinanceContactStatus())
                && !allOtherPartnersFinanceContactStatusComplete(teamStatus);
    }

    private boolean allOtherPartnersFinanceContactStatusComplete(ProjectTeamStatusResource teamStatus) {
        return teamStatus.checkForOtherPartners(status -> COMPLETE.equals(status.getFinanceContactStatus()));
    }
}
