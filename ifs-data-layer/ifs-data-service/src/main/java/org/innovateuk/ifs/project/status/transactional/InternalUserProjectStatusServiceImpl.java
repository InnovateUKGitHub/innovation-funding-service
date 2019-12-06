package org.innovateuk.ifs.project.status.transactional;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.competitionsetup.domain.DocumentConfig;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerService;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileService;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.APPROVED;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.SUBMITTED;
import static org.innovateuk.ifs.project.resource.ProjectState.*;
import static org.innovateuk.ifs.security.SecurityRuleUtil.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleAllMatch;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleAnyMatch;

/**
 * This service wraps the business logic around the statuses of Project(s).
 */
@Service
public class InternalUserProjectStatusServiceImpl extends AbstractProjectServiceImpl implements InternalUserProjectStatusService {

    @Autowired
    private SpendProfileService spendProfileService;

    @Autowired
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;

    @Autowired
    private GrantOfferLetterWorkflowHandler golWorkflowHandler;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @Autowired
    private MonitoringOfficerService monitoringOfficerService;

    @Override
    public ServiceResult<List<ProjectStatusResource>> getCompetitionStatus(long competitionId, String applicationSearchString) {
        return serviceSuccess(getProjectStatuses(() -> projectRepository.searchByCompetitionIdAndApplicationIdLike(competitionId, applicationSearchString)));
    }

    @Override
    public ServiceResult<List<ProjectStatusResource>> getPreviousCompetitionStatus(long competitionId) {
        return serviceSuccess(getProjectStatuses(() -> projectRepository.findByApplicationCompetitionIdAndProjectProcessActivityStateIn(competitionId, COMPLETED_STATES)));
    }

    @Override
    public ServiceResult<ProjectStatusResource> getProjectStatusByProject(Project project) {
        return serviceSuccess(getProjectStatusResourceByProject(project));
    }

    @Override
    public ServiceResult<ProjectStatusResource> getProjectStatusByProjectId(Long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent()) {
            return getProjectStatusByProject(project.get());
        }
        return ServiceResult.serviceFailure(new Error(GENERAL_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private List<ProjectStatusResource> getProjectStatuses(Supplier<List<Project>> projectSupplier) {
        return projectSupplier.get().stream()
                .map(this::getProjectStatusResourceByProject)
                .sorted(comparing(ProjectStatusResource::getApplicationNumber))
                .collect(toList());
    }

    private ProjectStatusResource getProjectStatusResourceByProject(Project project) {
        boolean locationPerPartnerRequired = project.getApplication().getCompetition().isLocationPerPartner();
        ProjectActivityStates partnerProjectLocationStatus = getPartnerProjectLocationStatus(project);
        ProjectActivityStates projectDetailsStatus = getProjectDetailsStatus(project, locationPerPartnerRequired, partnerProjectLocationStatus);
        ProjectActivityStates projectTeamStatus = getProjectTeamStatus(project);
        ProjectActivityStates financeChecksStatus = getFinanceChecksStatus(project);
        ProjectActivityStates bankDetailsStatus = getBankDetailsStatus(project);
        ProjectActivityStates spendProfileStatus = getSpendProfileStatus(project, financeChecksStatus);
        ProjectActivityStates documentsStatus = documentsState(project);

        return new ProjectStatusResource(
                project.getName(),
                project.getId(),
                project.getId().toString(),
                project.getApplication().getId(),
                project.getApplication().getId().toString(),
                project.getPartnerOrganisations().size(),
                project.getLeadOrganisation().map(PartnerOrganisation::getOrganisation).map(Organisation::getName).orElse(""),
                projectDetailsStatus,
                projectTeamStatus,
                bankDetailsStatus,
                financeChecksStatus,
                spendProfileStatus,
                getMonitoringOfficerStatus(project, createProjectDetailsStatus(project), locationPerPartnerRequired, partnerProjectLocationStatus),
                documentsStatus,
                getGrantOfferLetterState(project, bankDetailsStatus, spendProfileStatus, documentsStatus),
                getProjectSetupCompleteState(project, spendProfileStatus, documentsStatus),
                golWorkflowHandler.isSent(project),
                project.getProjectState());
    }
    private ProjectActivityStates getProjectDetailsStatus(Project project, boolean locationPerPartnerRequired, ProjectActivityStates partnerProjectLocationStatus) {
        if (locationPerPartnerRequired && PENDING.equals(partnerProjectLocationStatus)) {
            return PENDING;
        }
        return projectDetailsWorkflowHandler.isSubmitted(project) ?
                COMPLETE : PENDING;
    }

    private ProjectActivityStates getProjectTeamStatus(Project project) {
        return projectManagerAndFinanceContactsAllSelected(project) ?
                COMPLETE : PENDING;
    }

    private boolean projectManagerAndFinanceContactsAllSelected(Project project) {
        return getProjectManager(project).isPresent()
                && project.getOrganisations()
                .stream()
                .allMatch(org -> getFinanceContact(project, org).isPresent());
    }

    private ProjectActivityStates getPartnerProjectLocationStatus(Project project) {
        return simpleAnyMatch(project.getPartnerOrganisations(),
                              partnerOrganisation -> isBlank(partnerOrganisation.getPostcode())) ?
                PENDING : COMPLETE;
    }

    private ProjectActivityStates getFinanceChecksStatus(Project project) {
        boolean noSpendProfilesGenerated = project.getSpendProfiles().isEmpty();

        if(noSpendProfilesGenerated) {
            return project.getProjectState().isActive() ?
                    ACTION_REQUIRED : PENDING;
        }

        return COMPLETE;
    }

    private ProjectActivityStates getBankDetailsStatus(Project project) {

        // Show flag when there is any organisation awaiting approval.
        boolean incomplete = false;
        boolean started = false;
        for (Organisation organisation : project.getOrganisations()) {
            if (isOrganisationSeekingFunding(project.getId(), organisation.getId())) {
                Optional<BankDetails> bankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(project.getId(), organisation.getId());
                ProjectActivityStates financeContactStatus = createFinanceContactStatus(project, organisation);
                ProjectActivityStates organisationBankDetailsStatus = createBankDetailStatus(bankDetails, financeContactStatus);
                if (!bankDetails.isPresent() || organisationBankDetailsStatus.equals(ACTION_REQUIRED)) {
                    incomplete = true;
                }
                if (bankDetails.isPresent()) {
                    started = true;
                    if (organisationBankDetailsStatus.equals(PENDING)) {
                        return project.getProjectState().isActive() ?
                                ACTION_REQUIRED : PENDING;
                    }
                }
            }
        }
        if (!started) {
            return notStartedIfProjectActive(project.getProjectState());
        } else if (incomplete) {
            return PENDING;
        } else {
            return COMPLETE;
        }
    }

    private boolean isOrganisationSeekingFunding(long projectId, long organisationId) {
        return projectFinanceService.financeChecksDetails(projectId, organisationId)
                .andOnSuccessReturn(ProjectFinanceResource::isRequestingFunding)
                .getOptionalSuccessObject()
                .orElse(false);
    }

    private ProjectActivityStates getSpendProfileStatus(Project project, ProjectActivityStates financeCheckStatus) {
        ApprovalType approvalType = spendProfileService.getSpendProfileStatus(project.getId()).getSuccess();

        switch (approvalType) {
            case APPROVED:
                return COMPLETE;
            case REJECTED:
                return REJECTED;
            default:
                if (project.getSpendProfileSubmittedDate() != null) {
                    return actionRequiredIfProjectActive(project.getProjectState());
                }

                if (financeCheckStatus.equals(COMPLETE)) {
                    return PENDING;
                }

                return notStartedIfProjectActive(project.getProjectState());
        }
    }

    private ProjectActivityStates getMonitoringOfficerStatus(Project project,
                                                             ProjectActivityStates projectDetailsStatus,
                                                             final boolean locationPerPartnerRequired,
                                                             final ProjectActivityStates partnerProjectLocationStatus) {

        boolean monitoringOfficerExists = monitoringOfficerService.findMonitoringOfficerForProject(project.getId()).isSuccess();

        return createMonitoringOfficerCompetitionStatus(monitoringOfficerExists,
                                                        projectDetailsStatus,
                                                        locationPerPartnerRequired,
                                                        partnerProjectLocationStatus,
                                                        project.getProjectState());
    }

    private ProjectActivityStates createMonitoringOfficerCompetitionStatus(final boolean monitoringOfficerExists,
                                                                           final ProjectActivityStates leadProjectDetailsSubmitted,
                                                                           final boolean locationPerPartnerRequired,
                                                                           final ProjectActivityStates partnerProjectLocationStatus,
                                                                           final ProjectState projectState) {

        boolean allRequiredDetailsComplete;
        if (locationPerPartnerRequired) {
            allRequiredDetailsComplete = leadProjectDetailsSubmitted.equals(COMPLETE) && partnerProjectLocationStatus.equals(COMPLETE);
        } else {
            allRequiredDetailsComplete = leadProjectDetailsSubmitted.equals(COMPLETE);
        }

        return getMonitoringOfficerStatus(monitoringOfficerExists, allRequiredDetailsComplete, projectState);
    }

    private ProjectActivityStates getMonitoringOfficerStatus(final boolean monitoringOfficerExists,
                                                             final boolean allRequiredDetailsComplete,
                                                             final ProjectState projectState) {

        if (monitoringOfficerExists) {
            return COMPLETE;
        } else if (allRequiredDetailsComplete) {
            User user = loggedInUserSupplier.get();
            if (isSupport(user) || isInnovationLead(user) || isStakeholder(user)) {
                return notStartedIfProjectActive(projectState);
            } else {
                return projectState.isActive() ?
                ACTION_REQUIRED : PENDING;
            }
        } else {
            return notStartedIfProjectActive(projectState);
        }
    }

    private ProjectActivityStates getDocumentsStatus(Project project) {
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

        return getDocumentsState(projectDocuments,
                                 projectDocuments.size(),
                                 expectedDocuments.size(),
                                 project.getProjectState());
    }

    private ProjectActivityStates getDocumentsState(List<ProjectDocument> projectDocuments,
                                                    int actualNumberOfDocuments,
                                                    int expectedNumberOfDocuments,
                                                    ProjectState projectState) {
        if (actualNumberOfDocuments == expectedNumberOfDocuments
                && simpleAllMatch(projectDocuments, projectDocument -> APPROVED.equals(projectDocument.getStatus()))) {
            return COMPLETE;
        }
        // any state other than complete should show as pending for inactive projects
        if(!projectState.isActive()) {
            return PENDING;
        }

        if (simpleAnyMatch(projectDocuments, projectDocument -> SUBMITTED.equals(projectDocument.getStatus()))) {
            return ACTION_REQUIRED;
        }

        if (actualNumberOfDocuments == expectedNumberOfDocuments
                && simpleAllMatch(projectDocuments, projectDocument -> DocumentStatus.REJECTED.equals(projectDocument.getStatus()))) {
            return REJECTED;
        }

        return PENDING;
    }

    private ProjectActivityStates getGrantOfferLetterState(Project project, ProjectActivityStates bankDetailsStatus, ProjectActivityStates spendProfileStatus, ProjectActivityStates documentsStatus) {
        if (!projectContainsStage(project, ProjectSetupStage.GRANT_OFFER_LETTER)) {
            return COMPLETE;
        }
        if (COMPLETE.equals(documentsStatus)
                && COMPLETE.equals(spendProfileStatus)
                && COMPLETE.equals(bankDetailsStatus)) {
            if (golWorkflowHandler.isApproved(project)) {
                return COMPLETE;
            } else if (golWorkflowHandler.isRejected(project)) {
                return project.getProjectState().isActive() ?
                        REJECTED : PENDING;
            } else {
                if (golWorkflowHandler.isReadyToApprove(project)) {
                    return actionRequiredIfProjectActive(project.getProjectState());
                } else {
                    if (golWorkflowHandler.isSent(project)) {
                        return PENDING;
                    } else {
                        return actionRequiredIfProjectActive(project.getProjectState());
                    }
                }
            }
        } else {
            return notStartedIfProjectActive(project.getProjectState());
        }
    }

    private ProjectActivityStates getProjectSetupCompleteState(Project project, ProjectActivityStates spendProfileStatus, ProjectActivityStates documentStatus) {
        if (!projectContainsStage(project, ProjectSetupStage.PROJECT_SETUP_COMPLETE))  {
            return COMPLETE;
        }
        if (COMPLETE.equals(documentStatus) && COMPLETE.equals(spendProfileStatus)) {
            if (project.getProjectState() == LIVE || project.getProjectState() == UNSUCCESSFUL) {
                return COMPLETE;
            } else {
                return actionRequiredIfProjectActive(project.getProjectState());
            }
        } else {
            return notStartedIfProjectActive(project.getProjectState());
        }
    }

    private ProjectActivityStates documentsState(Project project) {
        if (!projectContainsStage(project, ProjectSetupStage.DOCUMENTS)) {
            return COMPLETE;
        }
        return getDocumentsStatus(project);
    }

    private boolean projectContainsStage(Project project, ProjectSetupStage projectSetupStage) {
        return project.getApplication().getCompetition().getProjectStages().stream()
                .filter(stage -> stage.getProjectSetupStage().equals(projectSetupStage))
                .findFirst()
                .isPresent();
    }

    private ProjectActivityStates actionRequiredIfProjectActive(ProjectState projectState) {
        return projectState.isActive() ?
                ACTION_REQUIRED :
                PENDING;
    }

    private ProjectActivityStates notStartedIfProjectActive(ProjectState projectState) {
        return projectState.isActive() ?
                NOT_STARTED :
                NOT_REQUIRED;
    }

    private ProjectActivityStates createFinanceContactStatus(Project project, Organisation partnerOrganisation) {

        return getFinanceContact(project, partnerOrganisation).isPresent() ?
                COMPLETE :
                ACTION_REQUIRED;
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

    private ProjectActivityStates createBankDetailStatus(final Optional<BankDetails> bankDetails, ProjectActivityStates financeContactStatus) {
        if (bankDetails.isPresent()) {
            return bankDetails.get().isApproved() ? COMPLETE : PENDING;
        } else if (COMPLETE.equals(financeContactStatus)) {
            return ACTION_REQUIRED;
        } else {
            return NOT_STARTED;
        }
    }
}
