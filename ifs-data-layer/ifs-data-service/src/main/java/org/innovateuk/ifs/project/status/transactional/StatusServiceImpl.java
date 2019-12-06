package org.innovateuk.ifs.project.status.transactional;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.core.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.spendprofile.configuration.workflow.SpendProfileWorkflowHandler;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState.SENT;
import static org.innovateuk.ifs.project.resource.ProjectState.LIVE;
import static org.innovateuk.ifs.project.resource.ProjectState.UNSUCCESSFUL;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * This service wraps the business logic around the statuses of Project(s).
 */
@Service
public class StatusServiceImpl extends AbstractProjectServiceImpl implements StatusService {

    @Autowired
    private GrantOfferLetterWorkflowHandler golWorkflowHandler;

    @Autowired
    private ProjectWorkflowHandler projectWorkflowHandler;

    @Autowired
    private ProjectProcessRepository projectProcessRepository;

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Autowired
    private SpendProfileWorkflowHandler spendProfileWorkflowHandler;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @Autowired
    private SpendProfileRepository spendProfileRepository;

    @Autowired
    private MonitoringOfficerService monitoringOfficerService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Override
    public ServiceResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId) {
        Project project = projectRepository.findById(projectId).get();
        ProjectProcess process = projectProcessRepository.findOneByTargetId(project.getId());
        PartnerOrganisation lead = project.getLeadOrganisation().get();

        Optional<ProjectUser> partnerUserForFilterUser = filterByUserId.flatMap(
                userId -> simpleFindFirst(project.getProjectUsers(),
                        pu -> pu.getUser().getId().equals(userId) && pu.getRole().isPartner()));

        List<PartnerOrganisation> partnerOrganisationsToInclude =
                simpleFilter(project.getPartnerOrganisations(), partner ->
                        partner.isLeadOrganisation() ||
                                (partnerUserForFilterUser.map(pu -> partner.getOrganisation().getId().equals(pu.getOrganisation().getId()))
                                        .orElse(true)));

        List<PartnerOrganisation> sortedOrganisationsToInclude
                = new PrioritySorting<>(partnerOrganisationsToInclude, lead, p -> p.getOrganisation().getName()).unwrap();

        List<ProjectPartnerStatusResource> projectPartnerStatusResources =
                simpleMap(sortedOrganisationsToInclude, partner -> getProjectPartnerStatus(project, partner));

        ProjectTeamStatusResource projectTeamStatusResource = new ProjectTeamStatusResource();
        projectTeamStatusResource.setPartnerStatuses(projectPartnerStatusResources);
        projectTeamStatusResource.setProjectState(process.getProcessState());
        projectTeamStatusResource.setProjectManagerAssigned(getProjectManager(project).isPresent());

        return serviceSuccess(projectTeamStatusResource);
    }

    private boolean projectManagerAndFinanceContactsAllSelected(Project project) {
        return getProjectManager(project).isPresent()
                && project.getOrganisations()
                .stream()
                .allMatch(org -> getFinanceContact(project, org).isPresent());
    }

    private boolean isOrganisationSeekingFunding(long projectId, long organisationId) {
        return projectFinanceService.financeChecksDetails(projectId, organisationId)
                .andOnSuccessReturn(ProjectFinanceResource::isRequestingFunding)
                .getOptionalSuccessObject()
                .orElse(false);
    }

    private ServiceResult<MonitoringOfficerResource> getExistingMonitoringOfficerForProject(Long projectId) {
        return monitoringOfficerService.findMonitoringOfficerForProject(projectId);
    }

    private boolean projectContainsStage(Project project, ProjectSetupStage projectSetupStage) {
        return project.getApplication().getCompetition().getProjectStages().stream()
                .anyMatch(stage -> stage.getProjectSetupStage().equals(projectSetupStage);
    }

    private ProjectPartnerStatusResource getProjectPartnerStatus(Project project, PartnerOrganisation partnerOrganisation) {
        Organisation organisation = partnerOrganisation.getOrganisation();
        OrganisationTypeEnum organisationType = OrganisationTypeEnum.getFromId(organisation.getOrganisationType().getId());
        boolean isLead = partnerOrganisation.isLeadOrganisation();
        if (partnerOrganisation.isPendingPartner()) {
            return new ProjectPartnerStatusResource(
                    organisation.getId(),
                    organisation.getName(),
                    organisationType,
                    NOT_STARTED,
                    NOT_STARTED,
                    NOT_STARTED,
                    NOT_STARTED,
                    NOT_STARTED,
                    NOT_STARTED,
                    NOT_STARTED,
                    NOT_STARTED,
                    NOT_STARTED,
                    NOT_STARTED,
                    NOT_STARTED,
                    false,
                    isLead,
                    true);
        }
        Optional<MonitoringOfficerResource> monitoringOfficer = getExistingMonitoringOfficerForProject(project.getId()).getOptionalSuccessObject();
        Optional<BankDetails> bankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(project.getId(), organisation.getId());
        Optional<SpendProfile> spendProfile = spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(), organisation.getId());

        boolean isQueryActionRequired = financeCheckService.isQueryActionRequired(project.getId(), organisation.getId()).getSuccess();

        ProjectActivityStates financeContactStatus = createFinanceContactStatus(project, organisation);
        ProjectActivityStates partnerProjectLocationStatus = createPartnerProjectLocationStatus(project, organisation);
        ProjectActivityStates bankDetailsStatus = createBankDetailStatus(project.getId(), organisation.getId(), bankDetails, financeContactStatus);
        ProjectActivityStates financeChecksStatus = createFinanceCheckStatus(project, organisation, isQueryActionRequired);
        ProjectActivityStates projectDetailsStatus = isLead ? createProjectDetailsStatus(project) : partnerProjectLocationStatus;
        ProjectActivityStates projectTeamStatus = isLead? createProjectTeamStatus(project) : financeContactStatus;
        ProjectActivityStates monitoringOfficerStatus = isLead ? createMonitoringOfficerStatus(monitoringOfficer.isPresent(), projectDetailsStatus) : NOT_REQUIRED;
        ProjectActivityStates spendProfileStatus = isLead ? createLeadSpendProfileStatus(project, financeChecksStatus, spendProfile) : createSpendProfileStatus(financeChecksStatus, spendProfile);
        ProjectActivityStates documentsStatus = isLead ? createDocumentStatus(project) : NOT_REQUIRED;
        ProjectActivityStates grantOfferLetterStatus = isLead ? createLeadGrantOfferLetterStatus(project) : createGrantOfferLetterStatus(project);
        ProjectActivityStates projectSetupCompleteStatus = createProjectSetupCompleteStatus(project, spendProfileStatus);

        boolean grantOfferLetterSentToProjectTeam =
                golWorkflowHandler.getExtendedState(project).
                        andOnSuccessReturn(GrantOfferLetterStateResource::isGeneratedGrantOfferLetterAlreadySentToProjectTeam).
                        getSuccess();

        return new ProjectPartnerStatusResource(
                organisation.getId(),
                organisation.getName(),
                organisationType,
                projectDetailsStatus,
                projectTeamStatus,
                monitoringOfficerStatus,
                bankDetailsStatus,
                financeChecksStatus,
                spendProfileStatus,
                documentsStatus,
                grantOfferLetterStatus,
                financeContactStatus,
                partnerProjectLocationStatus,
                projectSetupCompleteStatus,
                grantOfferLetterSentToProjectTeam,
                isLead,
                false);
    }

    private ProjectActivityStates createDocumentStatus(Project project) {

        if (!projectContainsStage(project, ProjectSetupStage.DOCUMENTS)) {
            return COMPLETE;
        }

        List<ProjectDocument> projectDocuments = project.getProjectDocuments();
        List<CompetitionDocument> expectedDocuments = project.getApplication().getCompetition().getCompetitionDocuments();
        if (!project.isCollaborativeProject()) {
            projectDocuments = projectDocuments.stream()
                    .filter(doc -> !COLLABORATION_AGREEMENT_TITLE.equals(doc.getCompetitionDocument().getTitle()))
                    .collect(toList());
            expectedDocuments = expectedDocuments.stream()
                    .filter(doc -> !COLLABORATION_AGREEMENT_TITLE.equals(doc.getTitle()))
                    .collect(toList());
        }
        int actualNumberOfDocuments = projectDocuments.size();
        int expectedNumberOfDocuments = expectedDocuments.size();

        if (actualNumberOfDocuments == expectedNumberOfDocuments && projectDocuments.stream()
                .allMatch(projectDocumentResource -> DocumentStatus.APPROVED.equals(projectDocumentResource.getStatus()))) {
            return COMPLETE;
        }

        if (actualNumberOfDocuments != expectedNumberOfDocuments || projectDocuments.stream()
                .anyMatch(projectDocumentResource -> DocumentStatus.UPLOADED.equals(projectDocumentResource.getStatus())
                        || DocumentStatus.REJECTED.equals(projectDocumentResource.getStatus()))) {
            return ACTION_REQUIRED;
        }

        return PENDING;
    }

    private ProjectActivityStates createFinanceContactStatus(Project project, Organisation partnerOrganisation) {

        return getFinanceContact(project, partnerOrganisation).isPresent() ?
                COMPLETE :
                ACTION_REQUIRED;
    }

    private ProjectActivityStates createPartnerProjectLocationStatus(Project project, Organisation organisation) {

        boolean locationPresent = project.getPartnerOrganisations().stream()
                .filter(partnerOrganisation -> partnerOrganisation.getOrganisation().getId().equals(organisation.getId()))
                .findFirst()
                .map(partnerOrganisation -> StringUtils.isNotBlank(partnerOrganisation.getPostcode()))
                .orElse(false);

        return locationPresent ? COMPLETE : ACTION_REQUIRED;
    }

    private ProjectActivityStates createProjectSetupCompleteStatus(Project project, ProjectActivityStates spendProfileStatus) {

        if (!project.getApplication().getCompetition().isLoan()) {
            return NOT_REQUIRED;
        }

        ProjectState state = projectWorkflowHandler.getState(project);
        if (LIVE.equals(state) || UNSUCCESSFUL.equals(state)) {
            return COMPLETE;
        } else if (spendProfileStatus.equals(COMPLETE)) {
            return PENDING;
        }
        return NOT_STARTED;
    }

    private ProjectActivityStates createProjectDetailsStatus(Project project) {
        boolean projectDetailsComplete = project.getAddress() != null
                && project.getTargetStartDate() != null
                && projectLocationsCompletedIfNecessary(project);
        return projectDetailsComplete ? COMPLETE : ACTION_REQUIRED;
    }

    private boolean projectLocationsCompletedIfNecessary(final Project project) {
        boolean locationsRequired = project.getApplication().getCompetition().isLocationPerPartner();
        if(!locationsRequired) {
            return true;
        }
        return project.getPartnerOrganisations()
                .stream()
                .noneMatch(org -> org.getPostcode() == null);
    }

    private ProjectActivityStates createProjectTeamStatus(Project project) {

        boolean complete = projectManagerAndFinanceContactsAllSelected(project);
        return complete? COMPLETE : ACTION_REQUIRED;
    }

    private ProjectActivityStates createMonitoringOfficerStatus(final boolean monitoringOfficerExists, final ProjectActivityStates leadProjectDetailsSubmitted) {
        if (leadProjectDetailsSubmitted.equals(COMPLETE)) {
            return monitoringOfficerExists ? COMPLETE : PENDING;
        } else {
            return NOT_STARTED;
        }
    }

    private ProjectActivityStates createBankDetailStatus(long projectId, long organisationId, final Optional<BankDetails> bankDetails, ProjectActivityStates financeContactStatus) {
        if (bankDetails.isPresent()) {
            return bankDetails.get().isApproved() ? COMPLETE : PENDING;
        } else if (!isOrganisationSeekingFunding(projectId, organisationId)) {
            return NOT_REQUIRED;
        } else if (COMPLETE.equals(financeContactStatus)) {
            return ACTION_REQUIRED;
        } else {
            return NOT_STARTED;
        }
    }


    private ProjectActivityStates createFinanceCheckStatus(final Project project, final Organisation organisation, boolean isAwaitingResponse) {
        PartnerOrganisation partnerOrg = partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(project.getId(), organisation.getId());
        if (financeChecksApproved(partnerOrg)) {
            return COMPLETE;
        } else if (isAwaitingResponse) {
            return ACTION_REQUIRED;
        }
        return PENDING;
    }

    private boolean financeChecksApproved(PartnerOrganisation partnerOrg) {
        return asList(EligibilityState.APPROVED, EligibilityState.NOT_APPLICABLE).contains(eligibilityWorkflowHandler.getState(partnerOrg)) &&
                asList(ViabilityState.APPROVED, ViabilityState.NOT_APPLICABLE).contains(viabilityWorkflowHandler.getState(partnerOrg));
    }

    private ProjectActivityStates createLeadSpendProfileStatus(final Project project, final  ProjectActivityStates financeCheckStatus, final Optional<SpendProfile> spendProfile) {
        ProjectActivityStates spendProfileStatus = createSpendProfileStatus(financeCheckStatus, spendProfile);
        if (COMPLETE.equals(spendProfileStatus) && !ApprovalType.APPROVED.equals(spendProfileWorkflowHandler.getApproval(project))) {
            return project.getSpendProfileSubmittedDate() != null ? PENDING : ACTION_REQUIRED;
        } else {
            return spendProfileStatus;
        }
    }

    private ProjectActivityStates createSpendProfileStatus(final ProjectActivityStates financeCheckStatus, final Optional<SpendProfile> spendProfile) {
        if (!spendProfile.isPresent()) {
            return NOT_STARTED;
        } else if (financeCheckStatus.equals(COMPLETE) && spendProfile.get().isMarkedAsComplete()) {
            return COMPLETE;
        } else {
            return ACTION_REQUIRED;
        }
    }

    private ProjectActivityStates createLeadGrantOfferLetterStatus(final Project project) {
        GrantOfferLetterState state = golWorkflowHandler.getState(project);
        if (SENT.equals(state)) {
            return ACTION_REQUIRED;
        } else if (GrantOfferLetterState.PENDING.equals(state) && project.getGrantOfferLetter() != null) {
            return PENDING;
        } else {
            return createGrantOfferLetterStatus(project);
        }
    }

    private ProjectActivityStates createGrantOfferLetterStatus(final Project project) {
        GrantOfferLetterState state = golWorkflowHandler.getState(project);
        if (GrantOfferLetterState.APPROVED.equals(state)) {
            return COMPLETE;
        } else if (GrantOfferLetterState.PENDING.equals(state)){
            return NOT_REQUIRED;
        } else {
            return PENDING;
        }
    }
}
