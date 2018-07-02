package org.innovateuk.ifs.project.status.transactional;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.finance.transactional.FinanceService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.core.util.ProjectUsersHelper;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.monitoringofficer.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
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
import java.util.stream.Collectors;

import static java.util.Collections.singleton;
import static java.util.Comparator.comparing;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isInnovationLead;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isSupport;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * This service wraps the business logic around the statuses of Project(s).
 */
@Service
public class StatusServiceImpl extends AbstractProjectServiceImpl implements StatusService {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ProjectUsersHelper projectUsersHelper;

    @Autowired
    private SpendProfileService spendProfileService;

    @Autowired
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;

    @Autowired
    private GrantOfferLetterWorkflowHandler golWorkflowHandler;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Override
    public ServiceResult<CompetitionProjectsStatusResource> getCompetitionStatus(Long competitionId, String applicationSearchString) {
        Competition competition = competitionRepository.findById(competitionId).get();
        List<Project> projects = projectRepository.searchByCompetitionIdAndApplicationIdLikeAndProjectStateNotIn(competitionId, applicationSearchString, singleton(ProjectState.WITHDRAWN));
        List<ProjectStatusResource> projectStatuses = projectStatuses(projects);
        CompetitionProjectsStatusResource competitionProjectsStatusResource
                = new CompetitionProjectsStatusResource(competition.getId(), competition.getName(), projectStatuses);

        return ServiceResult.serviceSuccess(competitionProjectsStatusResource);
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
        boolean locationPerPartnerRequired = project.getApplication().getCompetition().isLocationPerPartner();
        ProjectActivityStates projectDetailsStatus = getProjectDetailsStatus(project, locationPerPartnerRequired);
        ProjectActivityStates financeChecksStatus = getFinanceChecksStatus(project);

        ProcessRole leadProcessRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findById(leadProcessRole.getOrganisationId()).get();

        ProjectActivityStates partnerProjectLocationStatus = getPartnerProjectLocationStatus(project);

        return new ProjectStatusResource(
                project.getName(),
                project.getId(),
                project.getId().toString(),
                project.getApplication().getId(),
                project.getApplication().getId().toString(),
                getProjectPartnerCount(project.getId()),
                null != leadOrganisation ? leadOrganisation.getName() : "",
                projectDetailsStatus,
                getBankDetailsStatus(project),
                financeChecksStatus,
                getSpendProfileStatus(project, financeChecksStatus),
                getMonitoringOfficerStatus(project, createProjectDetailsStatus(project), locationPerPartnerRequired, partnerProjectLocationStatus),
                getOtherDocumentsStatus(project),
                getGrantOfferLetterStatus(project),
                getRoleSpecificGrantOfferLetterState(project),
                golWorkflowHandler.isSent(project));
    }

    private ProjectActivityStates getProjectDetailsStatus(Project project, boolean locationPerPartnerRequired) {
        for (Organisation organisation : project.getOrganisations()) {
            Optional<ProjectUser> financeContact = projectUsersHelper.getFinanceContact(project.getId(), organisation.getId());
            if (financeContact == null || !financeContact.isPresent()) {
                return PENDING;
            }
        }

        if (locationPerPartnerRequired && PENDING.equals(getPartnerProjectLocationStatus(project))) {
            return PENDING;
        }
        return createProjectDetailsCompetitionStatus(project);
    }

    private ProjectActivityStates createProjectDetailsCompetitionStatus(Project project) {
        return projectDetailsWorkflowHandler.isSubmitted(project) ? COMPLETE : PENDING;
    }

    private ProjectActivityStates getPartnerProjectLocationStatus(Project project) {

        return simpleAnyMatch(project.getPartnerOrganisations(), partnerOrganisation -> StringUtils.isBlank(partnerOrganisation.getPostcode()))? PENDING : COMPLETE;

    }

    private ProjectActivityStates getFinanceChecksStatus(Project project) {

        List<SpendProfile> spendProfile = spendProfileRepository.findByProjectId(project.getId());

        if (spendProfile.isEmpty()) {
            return ACTION_REQUIRED;
        }

        return COMPLETE;
    }

    private Integer getProjectPartnerCount(Long projectId) {
        return projectUsersHelper.getPartnerOrganisations(projectId).size();
    }

    private ProjectActivityStates getBankDetailsStatus(Project project) {
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
                        return ACTION_REQUIRED;
                    }
                }
            }
        }
        if (!started) {
            return NOT_STARTED;
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

    private ProjectActivityStates getSpendProfileStatus(Project project, ProjectActivityStates financeCheckStatus) {

        ApprovalType approvalType = spendProfileService.getSpendProfileStatus(project.getId()).getSuccess();
        switch (approvalType) {
            case APPROVED:
                return COMPLETE;
            case REJECTED:
                return REJECTED;
            default:
                if (project.getSpendProfileSubmittedDate() != null) {
                    return ACTION_REQUIRED;
                }

                if (financeCheckStatus.equals(COMPLETE)) {
                    return PENDING;
                }

                return NOT_STARTED;
        }
    }

    private ProjectActivityStates getMonitoringOfficerStatus(Project project,
                                                             ProjectActivityStates projectDetailsStatus,
                                                             final boolean locationPerPartnerRequired,
                                                             final ProjectActivityStates partnerProjectLocationStatus) {
        return createMonitoringOfficerCompetitionStatus(getExistingMonitoringOfficerForProject(project.getId()).getOptionalSuccessObject(),
                                                        projectDetailsStatus,
                                                        locationPerPartnerRequired,
                                                        partnerProjectLocationStatus);
    }

    private ServiceResult<MonitoringOfficer> getExistingMonitoringOfficerForProject(Long projectId) {
        return find(monitoringOfficerRepository.findOneByProjectId(projectId), notFoundError(MonitoringOfficer.class, projectId));
    }

    private ProjectActivityStates createMonitoringOfficerCompetitionStatus(final Optional<MonitoringOfficer> monitoringOfficer,
                                                                           final ProjectActivityStates leadProjectDetailsSubmitted,
                                                                           final boolean locationPerPartnerRequired,
                                                                           final ProjectActivityStates partnerProjectLocationStatus) {

        boolean allRequiredDetailsComplete;
        if (locationPerPartnerRequired) {
            allRequiredDetailsComplete = leadProjectDetailsSubmitted.equals(COMPLETE) && partnerProjectLocationStatus.equals(COMPLETE);
        } else {
            allRequiredDetailsComplete = leadProjectDetailsSubmitted.equals(COMPLETE);
        }

        return getMonitoringOfficerStatus(monitoringOfficer, allRequiredDetailsComplete);
    }

    private ProjectActivityStates getMonitoringOfficerStatus(final Optional<MonitoringOfficer> monitoringOfficer,
                                                             final boolean allRequiredDetailsComplete) {

        User user = loggedInUserSupplier.get();

        if (allRequiredDetailsComplete) {
            if(monitoringOfficer.isPresent()) {
                return COMPLETE;
            }
            else {
                if(isSupport(user) || isInnovationLead(user)){
                    return NOT_STARTED;
                } else {
                    return ACTION_REQUIRED;
                }
            }
        } else {
            return NOT_STARTED;
        }
    }

    private ProjectActivityStates getOtherDocumentsStatus(Project project) {

        if (ApprovalType.REJECTED.equals(project.getOtherDocumentsApproved())) {
            return REJECTED;
        }
        if (ApprovalType.APPROVED.equals(project.getOtherDocumentsApproved())) {
            return COMPLETE;
        }
        if (project.getDocumentsSubmittedDate() != null) {
            return ACTION_REQUIRED;
        }

        return PENDING;
    }

    private ProjectActivityStates getGrantOfferLetterStatus(Project project) {

        ApprovalType spendProfileApprovalType = spendProfileService.getSpendProfileStatus(project.getId()).getSuccess();

        if (project.getOfferSubmittedDate() == null && ApprovalType.APPROVED.equals(spendProfileApprovalType) && !golWorkflowHandler.isRejected(project)) {
            return PENDING;
        }

        if (project.getOfferSubmittedDate() == null && golWorkflowHandler.isRejected(project)) {
            return REJECTED;
        }

        if (project.getOfferSubmittedDate() != null && golWorkflowHandler.isApproved(project)) {
            return COMPLETE;
        }

        if (project.getOfferSubmittedDate() != null) {
            return ACTION_REQUIRED;
        }

        return NOT_STARTED;
    }

    private Map<Role, ProjectActivityStates> getRoleSpecificGrantOfferLetterState(Project project) {
        Map<Role, ProjectActivityStates> roleSpecificGolStates = new HashMap<Role, ProjectActivityStates>();

        ProjectActivityStates financeChecksStatus = getFinanceChecksStatus(project);
        ProjectActivityStates spendProfileStatus = getSpendProfileStatus(project, financeChecksStatus);
        if (ApprovalType.APPROVED.equals(project.getOtherDocumentsApproved()) && COMPLETE.equals(spendProfileStatus)) {
            if (golWorkflowHandler.isApproved(project)) {
                roleSpecificGolStates.put(COMP_ADMIN, COMPLETE);
            } else if (golWorkflowHandler.isRejected(project)) {
                roleSpecificGolStates.put(COMP_ADMIN, REJECTED);
            } else {
                if (golWorkflowHandler.isReadyToApprove(project)) {
                    roleSpecificGolStates.put(COMP_ADMIN, ACTION_REQUIRED);
                } else {
                    if (golWorkflowHandler.isSent(project)) {
                        roleSpecificGolStates.put(COMP_ADMIN, PENDING);
                    } else {
                        roleSpecificGolStates.put(COMP_ADMIN, ACTION_REQUIRED);
                    }
                }
            }
        } else {
            roleSpecificGolStates.put(COMP_ADMIN, NOT_STARTED);
        }
        return roleSpecificGolStates;
    }

    @Override
    public ServiceResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId) {
        Project project = projectRepository.findById(projectId).get();
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

        return serviceSuccess(projectTeamStatusResource);
    }

    private ProjectPartnerStatusResource getProjectPartnerStatus(Project project, Organisation partnerOrganisation) {
        ProcessRole leadRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findById(leadRole.getOrganisationId()).orElse(null);
        Optional<MonitoringOfficer> monitoringOfficer = getExistingMonitoringOfficerForProject(project.getId()).getOptionalSuccessObject();
        Optional<BankDetails> bankDetails = Optional.ofNullable(bankDetailsRepository.findByProjectIdAndOrganisationId(project.getId(), partnerOrganisation.getId()));
        Optional<SpendProfile> spendProfile = spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(), partnerOrganisation.getId());
        OrganisationTypeEnum organisationType = OrganisationTypeEnum.getFromId(partnerOrganisation.getOrganisationType().getId());

        boolean isQueryActionRequired = financeCheckService.isQueryActionRequired(project.getId(),partnerOrganisation.getId()).getSuccess();
        boolean isLead = partnerOrganisation.equals(leadOrganisation);

        ProjectActivityStates financeContactStatus = createFinanceContactStatus(project, partnerOrganisation);
        ProjectActivityStates partnerProjectLocationStatus = createPartnerProjectLocationStatus(project, partnerOrganisation);
        ProjectActivityStates bankDetailsStatus = createBankDetailStatus(project.getId(), project.getApplication().getId(), partnerOrganisation.getId(), bankDetails, financeContactStatus);
        ProjectActivityStates financeChecksStatus = createFinanceCheckStatus(project, partnerOrganisation, isQueryActionRequired);
        ProjectActivityStates projectDetailsStatus = isLead ? createProjectDetailsStatus(project) : financeContactStatus;
        ProjectActivityStates monitoringOfficerStatus = isLead ? createMonitoringOfficerStatus(monitoringOfficer, projectDetailsStatus) : NOT_REQUIRED;
        ProjectActivityStates spendProfileStatus = isLead ? createLeadSpendProfileStatus(project, financeChecksStatus, spendProfile) : createSpendProfileStatus(financeChecksStatus, spendProfile);
        ProjectActivityStates otherDocumentsStatus = isLead ? createOtherDocumentStatus(project) : NOT_REQUIRED;
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
                monitoringOfficerStatus,
                bankDetailsStatus,
                financeChecksStatus,
                spendProfileStatus,
                otherDocumentsStatus,
                grantOfferLetterStatus,
                financeContactStatus,
                partnerProjectLocationStatus,
                grantOfferLetterSentToProjectTeam,
                isLead);
    }
}
