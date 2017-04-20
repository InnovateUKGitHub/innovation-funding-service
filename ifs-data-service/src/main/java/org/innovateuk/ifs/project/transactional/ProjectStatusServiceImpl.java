package org.innovateuk.ifs.project.transactional;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.monitoringofficer.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.financecheck.domain.SpendProfile;
import org.innovateuk.ifs.project.financecheck.transactional.SpendProfileService;
import org.innovateuk.ifs.project.gol.workflow.configuration.GOLWorkflowHandler;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.users.ProjectUsersHelper;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * This service wraps the business logic around the statuses of Project(s).
 */
@Service
public class ProjectStatusServiceImpl extends AbstractProjectServiceImpl implements ProjectStatusService {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ProjectUsersHelper projectUsersHelper;

    @Autowired
    private SpendProfileService spendProfileService;

    @Autowired
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;

    @Autowired
    private GOLWorkflowHandler golWorkflowHandler;

    @Autowired
    private FinanceRowService financeRowService;

    @Override
    public ServiceResult<CompetitionProjectsStatusResource> getCompetitionStatus(Long competitionId) {
        Competition competition = competitionRepository.findOne(competitionId);
        List<Project> projects = projectRepository.findByApplicationCompetitionId(competitionId);
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
        Project project = projectRepository.findOne(projectId);
        if (null != project) {
            return ServiceResult.serviceSuccess(getProjectStatusResourceByProject(project));
        }
        return ServiceResult.serviceFailure(new Error(GENERAL_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private ProjectStatusResource getProjectStatusResourceByProject(Project project) {

        ProjectActivityStates projectDetailsStatus = getProjectDetailsStatus(project);
        ProjectActivityStates financeChecksStatus = getFinanceChecksStatus(project);

        ProcessRole leadProcessRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findOne(leadProcessRole.getOrganisationId());

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
                getMonitoringOfficerStatus(project, createProjectDetailsStatus(project)),
                getOtherDocumentsStatus(project),
                getGrantOfferLetterStatus(project),
                getRoleSpecificGrantOfferLetterState(project),
                golWorkflowHandler.isSent(project));
    }

    private Integer getProjectPartnerCount(Long projectId) {
        return projectUsersHelper.getPartnerOrganisations(projectId).size();
    }

    private ProjectActivityStates getProjectDetailsStatus(Project project) {
        for (Organisation organisation : project.getOrganisations()) {
            Optional<ProjectUser> financeContact = projectUsersHelper.getFinanceContact(project.getId(), organisation.getId());
            if (financeContact == null || !financeContact.isPresent()) {
                return PENDING;
            }
        }
        return createProjectDetailsCompetitionStatus(project);
    }

    private ProjectActivityStates createProjectDetailsCompetitionStatus(Project project) {
        return projectDetailsWorkflowHandler.isSubmitted(project) ? COMPLETE : PENDING;
    }

    private boolean isOrganisationSeekingFunding(Long projectId, Long applicationId, Long organisationId) {
        Optional<Boolean> result = financeRowService.organisationSeeksFunding(projectId, applicationId, organisationId).getOptionalSuccessObject();
        return result.map(Boolean::booleanValue).orElse(false);
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

    private ProjectActivityStates getFinanceChecksStatus(Project project) {

        List<SpendProfile> spendProfile = spendProfileRepository.findByProjectId(project.getId());

        if (spendProfile.isEmpty()) {
            return ACTION_REQUIRED;
        }

        return COMPLETE;
    }

    private ProjectActivityStates getSpendProfileStatus(Project project, ProjectActivityStates financeCheckStatus) {

        ApprovalType approvalType = spendProfileService.getSpendProfileStatusByProjectId(project.getId()).getSuccessObject();
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

    private ProjectActivityStates getMonitoringOfficerStatus(Project project, ProjectActivityStates projectDetailsStatus) {
        return createMonitoringOfficerCompetitionStatus(getExistingMonitoringOfficerForProject(project.getId()).getOptionalSuccessObject(), projectDetailsStatus);
    }

    private ServiceResult<MonitoringOfficer> getExistingMonitoringOfficerForProject(Long projectId) {
        return find(monitoringOfficerRepository.findOneByProjectId(projectId), notFoundError(MonitoringOfficer.class, projectId));
    }

    private ProjectActivityStates createMonitoringOfficerCompetitionStatus(final Optional<MonitoringOfficer> monitoringOfficer, final ProjectActivityStates leadProjectDetailsSubmitted) {
        if (leadProjectDetailsSubmitted.equals(COMPLETE)) {
            return monitoringOfficer.isPresent() ? COMPLETE : ACTION_REQUIRED;
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

        ApprovalType spendProfileApprovalType = spendProfileService.getSpendProfileStatusByProjectId(project.getId()).getSuccessObject();

        if (project.getOfferSubmittedDate() == null && ApprovalType.APPROVED.equals(spendProfileApprovalType)) {
            return PENDING;
        }

        if (project.getOfferSubmittedDate() != null) {
            if (golWorkflowHandler.isApproved(project)) {
                return COMPLETE;
            }
        }

        if (project.getOfferSubmittedDate() != null) {
            return ACTION_REQUIRED;
        }

        return NOT_STARTED;
    }

    private Map<UserRoleType, ProjectActivityStates> getRoleSpecificGrantOfferLetterState(Project project) {
        Map<UserRoleType, ProjectActivityStates> roleSpecificGolStates = new HashMap<UserRoleType, ProjectActivityStates>();

        ProjectActivityStates financeChecksStatus = getFinanceChecksStatus(project);
        ProjectActivityStates spendProfileStatus = getSpendProfileStatus(project, financeChecksStatus);
        if (ApprovalType.APPROVED.equals(project.getOtherDocumentsApproved()) && COMPLETE.equals(spendProfileStatus)) {
            if (golWorkflowHandler.isApproved(project)) {
                roleSpecificGolStates.put(COMP_ADMIN, COMPLETE);
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
}
