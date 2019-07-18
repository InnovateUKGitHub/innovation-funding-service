package org.innovateuk.ifs.project.status.transactional;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.competitionsetup.domain.DocumentConfig;
import org.innovateuk.ifs.finance.transactional.FinanceService;
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
import org.innovateuk.ifs.project.core.transactional.PartnerOrganisationService;
import org.innovateuk.ifs.project.core.util.ProjectUsersHelper;
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
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerService;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.spendprofile.configuration.workflow.SpendProfileWorkflowHandler;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileService;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.APPROVED;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.SUBMITTED;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState.SENT;
import static org.innovateuk.ifs.project.resource.ProjectState.COMPLETED_STATES;
import static org.innovateuk.ifs.security.SecurityRuleUtil.*;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * This service wraps the business logic around the statuses of Project(s).
 */
@Service
public class StatusServiceImpl extends AbstractProjectServiceImpl implements StatusService {

    @Autowired
    private ProjectUsersHelper projectUsersHelper;

    @Autowired
    private SpendProfileService spendProfileService;

    @Autowired
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;

    @Autowired
    private GrantOfferLetterWorkflowHandler golWorkflowHandler;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private PartnerOrganisationService partnerOrganisationService;

    @Autowired
    private ProjectProcessRepository projectProcessRepository;

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Autowired
    private SpendProfileWorkflowHandler spendProfileWorkflowHandler;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @Autowired
    private SpendProfileRepository spendProfileRepository;

    @Autowired
    private MonitoringOfficerService monitoringOfficerService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Override
    public ServiceResult<CompetitionProjectsStatusResource> getCompetitionStatus(long competitionId, String applicationSearchString) {
        return getCompetitionStatus(competitionId, () -> projectRepository.searchByCompetitionIdAndApplicationIdLike(competitionId, applicationSearchString));
    }

    @Override
    public ServiceResult<CompetitionProjectsStatusResource> getPreviousCompetitionStatus(long competitionId) {
        return getCompetitionStatus(competitionId, () -> projectRepository.findByApplicationCompetitionIdAndProjectProcessActivityStateIn(competitionId, COMPLETED_STATES));
    }

    private ServiceResult<CompetitionProjectsStatusResource> getCompetitionStatus(long competitionId, Supplier<List<Project>> projectSupplier) {
        return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId))
                .andOnSuccessReturn(competition -> {
                    List<Project> projects = projectSupplier.get();
                    List<ProjectStatusResource> projectStatuses = projectStatuses(projects);
                    return new CompetitionProjectsStatusResource(competition.getId(), competition.getName(), projectStatuses);
                });
    }

    private List<ProjectStatusResource> projectStatuses(List<Project> projects) {
        return projects.stream()
                .map(this::getProjectStatusResourceByProject)
                .sorted(comparing(ProjectStatusResource::getApplicationNumber))
                .collect(Collectors.toList());
    }

    @Override
    public ServiceResult<ProjectStatusResource> getProjectStatusByProjectId(Long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent()) {
            return getProjectStatusByProject(project.get());
        }
        return ServiceResult.serviceFailure(new Error(GENERAL_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    public ServiceResult<ProjectStatusResource> getProjectStatusByProject(Project project) {
        return serviceSuccess(getProjectStatusResourceByProject(project));
    }

    private ProjectStatusResource getProjectStatusResourceByProject(Project project) {
        ProjectProcess process = projectProcessRepository.findOneByTargetId(project.getId());
        boolean locationPerPartnerRequired = project.getApplication().getCompetition().isLocationPerPartner();
        ProjectActivityStates projectDetailsStatus = getProjectDetailsStatus(project, locationPerPartnerRequired, process.getProcessState());
        ProjectActivityStates projectTeamStatus = getProjectTeamStatus(project);
        ProjectActivityStates financeChecksStatus = getFinanceChecksStatus(project, process.getProcessState());

        ProcessRole leadProcessRole = project.getApplication().getLeadApplicantProcessRole();
        Optional<Organisation> leadOrganisation = organisationRepository.findById(leadProcessRole.getOrganisationId());

        ProjectActivityStates partnerProjectLocationStatus = getPartnerProjectLocationStatus(project);
        ProjectActivityStates bankDetailsStatus = getBankDetailsStatus(project, process.getProcessState());

        return new ProjectStatusResource(
                project.getName(),
                project.getId(),
                project.getId().toString(),
                project.getApplication().getId(),
                project.getApplication().getId().toString(),
                getProjectPartnerCount(project.getId()),
                leadOrganisation.map(Organisation::getName).orElse(""),
                projectDetailsStatus,
                projectTeamStatus,
                bankDetailsStatus,
                financeChecksStatus,
                getSpendProfileStatus(project, financeChecksStatus, process.getProcessState()),
                getMonitoringOfficerStatus(project, createProjectDetailsStatus(project), locationPerPartnerRequired, partnerProjectLocationStatus, process.getProcessState()),
                getDocumentsStatus(project, process.getProcessState()),
                getGrantOfferLetterStatus(project, process.getProcessState()),
                getRoleSpecificGrantOfferLetterState(project, process.getProcessState(), bankDetailsStatus),
                golWorkflowHandler.isSent(project),
                process.getProcessState());
    }

    private ProjectActivityStates getProjectDetailsStatus(Project project, boolean locationPerPartnerRequired, ProjectState processState) {
        if (locationPerPartnerRequired && PENDING.equals(getPartnerProjectLocationStatus(project))) {
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

    private ProjectActivityStates getFinanceChecksStatus(Project project, ProjectState processState) {

        boolean noSpendProfilesGenerated = spendProfileRepository.findByProjectId(project.getId()).isEmpty();

        if(noSpendProfilesGenerated) {
            return processState.isActive() ?
                    ACTION_REQUIRED : PENDING;
        }

        return COMPLETE;
    }

    private int getProjectPartnerCount(Long projectId) {
        return projectUsersHelper.getPartnerOrganisations(projectId).size();
    }

    private ProjectActivityStates getBankDetailsStatus(Project project, ProjectState processState) {
        // Show flag when there is any organisation awaiting approval.
        boolean incomplete = false;
        boolean started = false;
        for (Organisation organisation : project.getOrganisations()) {
            if (isOrganisationSeekingFunding(project.getId(), project.getApplication().getId(), organisation.getId())) {
                Optional<BankDetails> bankDetails = Optional.ofNullable(bankDetailsRepository.findByProjectIdAndOrganisationId(project.getId(), organisation.getId()));
                ProjectActivityStates financeContactStatus = createFinanceContactStatus(project, organisation);
                ProjectActivityStates organisationBankDetailsStatus = createBankDetailStatus(project.getId(), project.getApplication().getId(), organisation.getId(), bankDetails, financeContactStatus);
                if (!bankDetails.isPresent() || organisationBankDetailsStatus.equals(ACTION_REQUIRED)) {
                    incomplete = true;
                }
                if (bankDetails.isPresent()) {
                    started = true;
                    if (organisationBankDetailsStatus.equals(PENDING)) {
                        return processState.isActive() ?
                                ACTION_REQUIRED : PENDING;
                    }
                }
            }
        }
        if (!started) {
            return notStartedIfProjectActive(processState);
        } else if (incomplete) {
            return PENDING;
        } else {
            return COMPLETE;
        }
    }

    private boolean isOrganisationSeekingFunding(Long projectId, Long applicationId, Long organisationId) {
        Optional<Boolean> result = financeService.organisationSeeksFunding(projectId, applicationId, organisationId).getOptionalSuccessObject();
        return result.orElse(false);
    }

    private ProjectActivityStates getSpendProfileStatus(Project project, ProjectActivityStates financeCheckStatus, ProjectState processState) {

        ApprovalType approvalType = spendProfileService.getSpendProfileStatus(project.getId()).getSuccess();

        switch (approvalType) {
            case APPROVED:
                return COMPLETE;
            case REJECTED:
                return REJECTED;
            default:
                if (project.getSpendProfileSubmittedDate() != null) {
                    return actionRequiredIfProjectActive(processState);
                }

                if (financeCheckStatus.equals(COMPLETE)) {
                    return PENDING;
                }

                return notStartedIfProjectActive(processState);
        }
    }

    private ProjectActivityStates getMonitoringOfficerStatus(Project project,
                                                             ProjectActivityStates projectDetailsStatus,
                                                             final boolean locationPerPartnerRequired,
                                                             final ProjectActivityStates partnerProjectLocationStatus,
                                                             ProjectState processState) {

        boolean monitoringOfficerExists = monitoringOfficerService.findMonitoringOfficerForProject(project.getId()).isSuccess();

        return createMonitoringOfficerCompetitionStatus(monitoringOfficerExists,
                                                        projectDetailsStatus,
                                                        locationPerPartnerRequired,
                                                        partnerProjectLocationStatus,
                                                        processState);
    }

    private ServiceResult<MonitoringOfficerResource> getExistingMonitoringOfficerForProject(Long projectId) {
        return monitoringOfficerService.findMonitoringOfficerForProject(projectId);
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

        if (allRequiredDetailsComplete) {
            if (monitoringOfficerExists) {
                return COMPLETE;
            } else {
                User user = loggedInUserSupplier.get();
                if (isSupport(user) || isInnovationLead(user) || isStakeholder(user)) {
                    return notStartedIfProjectActive(projectState);
                } else {
                    return projectState.isActive() ?
                    ACTION_REQUIRED : PENDING;
                }
            }
        } else {
            return notStartedIfProjectActive(projectState);
        }
    }

    private ProjectActivityStates getDocumentsStatus(Project project, ProjectState processState) {

        List<ProjectDocument> projectDocuments = project.getProjectDocuments();

        List<CompetitionDocument> expectedDocuments = project.getApplication().getCompetition().getCompetitionDocuments();

        List<PartnerOrganisationResource> partnerOrganisations =
                partnerOrganisationService.getProjectPartnerOrganisations(project.getId()).getSuccess();

        if (partnerOrganisations.size() == 1) {

            List<String> documentNames = expectedDocuments.stream()
                    .map(DocumentConfig::getTitle)
                    .collect(Collectors.toList());

            if (documentNames.contains(COLLABORATION_AGREEMENT_TITLE)) {
                return getDocumentsState(projectDocuments,
                                         projectDocuments.size(),
                                         expectedDocuments.size() - 1,
                                         processState);
            }
        }

        return getDocumentsState(projectDocuments,
                                 projectDocuments.size(),
                                 expectedDocuments.size(),
                                 processState);
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

    private ProjectActivityStates getGrantOfferLetterStatus(Project project, ProjectState processState) {

        ApprovalType spendProfileApprovalType = spendProfileService.getSpendProfileStatus(project.getId()).getSuccess();


        if (project.getOfferSubmittedDate() != null && golWorkflowHandler.isApproved(project)) {
            return COMPLETE;
        }

        if (project.getOfferSubmittedDate() == null && ApprovalType.APPROVED.equals(spendProfileApprovalType) && !golWorkflowHandler.isRejected(project)) {
            return PENDING;
        }

        if (project.getOfferSubmittedDate() == null && golWorkflowHandler.isRejected(project)) {
            return REJECTED;
        }


        if (project.getOfferSubmittedDate() != null) {
            return processState.isActive() ?
                    ACTION_REQUIRED : PENDING;
        }

        return notStartedIfProjectActive(processState);
    }

    private Map<Role, ProjectActivityStates> getRoleSpecificGrantOfferLetterState(Project project, ProjectState processState, ProjectActivityStates bankDetailsStatus) {
        Map<Role, ProjectActivityStates> roleSpecificGolStates = new HashMap<Role, ProjectActivityStates>();
            ProjectActivityStates financeChecksStatus = getFinanceChecksStatus(project, processState);
            ProjectActivityStates spendProfileStatus = getSpendProfileStatus(project, financeChecksStatus, processState);

            if (documentsApproved(project, processState)
                    && COMPLETE.equals(spendProfileStatus)
                    && COMPLETE.equals(bankDetailsStatus)) {
                if (golWorkflowHandler.isApproved(project)) {
                    roleSpecificGolStates.put(COMP_ADMIN, COMPLETE);
                } else if (golWorkflowHandler.isRejected(project)) {
                    ProjectActivityStates state = processState.isActive() ?
                            REJECTED : PENDING;
                    roleSpecificGolStates.put(COMP_ADMIN, state);
                } else {
                    if (golWorkflowHandler.isReadyToApprove(project)) {
                        roleSpecificGolStates.put(COMP_ADMIN, actionRequiredIfProjectActive(processState));
                    } else {
                        if (golWorkflowHandler.isSent(project)) {
                            roleSpecificGolStates.put(COMP_ADMIN, PENDING);
                        } else {
                            roleSpecificGolStates.put(COMP_ADMIN, actionRequiredIfProjectActive(processState));
                        }
                    }
                }
            } else {
                roleSpecificGolStates.put(COMP_ADMIN, notStartedIfProjectActive(processState));
            }

            return roleSpecificGolStates;
    }

    private boolean documentsApproved(Project project, ProjectState state) {
        return COMPLETE.equals(getDocumentsStatus(project, state));
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

    @Override
    public ServiceResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId) {
        Project project = projectRepository.findById(projectId).get();
        ProjectProcess process = projectProcessRepository.findOneByTargetId(project.getId());
        ProcessRole leadRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findById(leadRole.getOrganisationId()).orElse(null);

        Optional<ProjectUser> partnerUserForFilterUser = filterByUserId.flatMap(
                userId -> simpleFindFirst(project.getProjectUsers(),
                        pu -> pu.getUser().getId().equals(userId) && pu.getRole().isPartner()));

        List<Organisation> partnerOrganisationsToInclude =
                simpleFilter(project.getOrganisations(), partner ->
                        partner.getId().equals(leadOrganisation.getId()) ||
                                (partnerUserForFilterUser.map(pu -> partner.getId().equals(pu.getOrganisation().getId()))
                                        .orElse(true)));

        List<Organisation> sortedOrganisationsToInclude
                = new PrioritySorting<>(partnerOrganisationsToInclude, leadOrganisation, Organisation::getName).unwrap();

        List<ProjectPartnerStatusResource> projectPartnerStatusResources =
                simpleMap(sortedOrganisationsToInclude, partner -> getProjectPartnerStatus(project, partner));

        ProjectTeamStatusResource projectTeamStatusResource = new ProjectTeamStatusResource();
        projectTeamStatusResource.setPartnerStatuses(projectPartnerStatusResources);
        projectTeamStatusResource.setProjectState(process.getProcessState());
        projectTeamStatusResource.setProjectManagerAssigned(getProjectManager(project).isPresent());

        return serviceSuccess(projectTeamStatusResource);
    }

    private ProjectPartnerStatusResource getProjectPartnerStatus(Project project, Organisation partnerOrganisation) {
        ProcessRole leadRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findById(leadRole.getOrganisationId()).orElse(null);
        Optional<MonitoringOfficerResource> monitoringOfficer = getExistingMonitoringOfficerForProject(project.getId()).getOptionalSuccessObject();
        Optional<BankDetails> bankDetails = Optional.ofNullable(bankDetailsRepository.findByProjectIdAndOrganisationId(project.getId(), partnerOrganisation.getId()));
        Optional<SpendProfile> spendProfile = spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(), partnerOrganisation.getId());
        OrganisationTypeEnum organisationType = OrganisationTypeEnum.getFromId(partnerOrganisation.getOrganisationType().getId());

        boolean isQueryActionRequired = financeCheckService.isQueryActionRequired(project.getId(), partnerOrganisation.getId()).getSuccess();
        boolean isLead = partnerOrganisation.equals(leadOrganisation);

        ProjectActivityStates financeContactStatus = createFinanceContactStatus(project, partnerOrganisation);
        ProjectActivityStates partnerProjectLocationStatus = createPartnerProjectLocationStatus(project, partnerOrganisation);
        ProjectActivityStates bankDetailsStatus = createBankDetailStatus(project.getId(), project.getApplication().getId(), partnerOrganisation.getId(), bankDetails, financeContactStatus);
        ProjectActivityStates financeChecksStatus = createFinanceCheckStatus(project, partnerOrganisation, isQueryActionRequired);
        ProjectActivityStates projectDetailsStatus = isLead ? createProjectDetailsStatus(project) : partnerProjectLocationStatus;
        ProjectActivityStates projectTeamStatus = isLead? createProjectTeamStatus(project) : financeContactStatus;
        ProjectActivityStates monitoringOfficerStatus = isLead ? createMonitoringOfficerStatus(monitoringOfficer.isPresent(), projectDetailsStatus) : NOT_REQUIRED;
        ProjectActivityStates spendProfileStatus = isLead ? createLeadSpendProfileStatus(project, financeChecksStatus, spendProfile) : createSpendProfileStatus(financeChecksStatus, spendProfile);
        ProjectActivityStates documentsStatus = isLead ? createDocumentStatus(project) : NOT_REQUIRED;
        ProjectActivityStates grantOfferLetterStatus = isLead ? createLeadGrantOfferLetterStatus(project) : createGrantOfferLetterStatus(project);

        boolean grantOfferLetterSentToProjectTeam =
                golWorkflowHandler.getExtendedState(project).
                        andOnSuccessReturn(GrantOfferLetterStateResource::isGeneratedGrantOfferLetterAlreadySentToProjectTeam).
                        getSuccess();

        return new ProjectPartnerStatusResource(
                partnerOrganisation.getId(),
                partnerOrganisation.getName(),
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
                grantOfferLetterSentToProjectTeam,
                isLead);
    }

    private ProjectActivityStates createDocumentStatus(Project project) {

        List<ProjectDocument> projectDocuments = project.getProjectDocuments();

        int expectedNumberOfDocuments = expectedNumberOfDocuments(project);
        int actualNumberOfDocuments = projectDocuments.size();

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

    private int expectedNumberOfDocuments(Project project) {
        List<PartnerOrganisation> partnerOrganisations = project.getPartnerOrganisations();
        List<CompetitionDocument> expectedDocuments = project.getApplication().getCompetition().getCompetitionDocuments();

        int expectedNumberOfDocuments = expectedDocuments.size();
        if (partnerOrganisations.size() == 1) {
            List<String> documentNames = expectedDocuments.stream().map(CompetitionDocument::getTitle).collect(Collectors.toList());
            if (documentNames.contains(COLLABORATION_AGREEMENT_TITLE)) {
                expectedNumberOfDocuments = expectedDocuments.size() - 1;
            }
        }
        return expectedNumberOfDocuments;
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

    private ProjectActivityStates createBankDetailStatus(Long projectId, Long applicationId, Long organisationId, final Optional<BankDetails> bankDetails, ProjectActivityStates financeContactStatus) {
        if (bankDetails.isPresent()) {
            return bankDetails.get().isApproved() ? COMPLETE : PENDING;
        } else if (!isSeekingFunding(projectId, applicationId, organisationId)) {
            return NOT_REQUIRED;
        } else if (COMPLETE.equals(financeContactStatus)) {
            return ACTION_REQUIRED;
        } else {
            return NOT_STARTED;
        }
    }

    private boolean isSeekingFunding(Long projectId, Long applicationId, Long organisationId) {
        return financeService.organisationSeeksFunding(projectId, applicationId, organisationId)
                .getOptionalSuccessObject()
                .orElse(false);
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
