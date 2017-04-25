package org.innovateuk.ifs.project.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.invite.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.monitoringofficer.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.gol.workflow.configuration.GOLWorkflowHandler;
import org.innovateuk.ifs.project.mapper.ProjectMapper;
import org.innovateuk.ifs.project.mapper.ProjectUserMapper;
import org.innovateuk.ifs.project.monitoringofficer.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.project.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Abstract service for handling project service functionality.
 */
public class AbstractProjectServiceImpl extends BaseTransactionalService {

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected ProjectMapper projectMapper;

    @Autowired
    protected ProjectUserRepository projectUserRepository;

    @Autowired
    protected ProjectUserMapper projectUserMapper;

    @Autowired
    protected MonitoringOfficerRepository monitoringOfficerRepository;

    @Autowired
    protected BankDetailsRepository bankDetailsRepository;

    @Autowired
    protected SpendProfileRepository spendProfileRepository;

    @Autowired
    protected FinanceRowService financeRowService;

    @Autowired
    protected FinanceCheckService financeCheckService;

    @Autowired
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;

    @Autowired
    private GOLWorkflowHandler golWorkflowHandler;

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Autowired
    protected PartnerOrganisationRepository partnerOrganisationRepository;

    List<ProjectUser> getProjectUsersByProjectId(Long projectId) {
        return projectUserRepository.findByProjectId(projectId);
    }

    protected ProjectActivityStates createOtherDocumentStatus(final Project project) {

        if (ApprovalType.APPROVED.equals(project.getOtherDocumentsApproved())) {
            return COMPLETE;
        }

        if (ApprovalType.REJECTED.equals(project.getOtherDocumentsApproved())) {
            return ACTION_REQUIRED;
        }

        if (ApprovalType.UNSET.equals(project.getOtherDocumentsApproved()) && project.getDocumentsSubmittedDate() != null) {
            return PENDING;
        }

        return ACTION_REQUIRED;
    }

    protected ProjectActivityStates createFinanceContactStatus(Project project, Organisation partnerOrganisation) {

        Optional<ProjectUser> financeContactForOrganisation = simpleFindFirst(project.getProjectUsers(), pu ->
                pu.getRole().isFinanceContact() &&
                        pu.getOrganisation().getId().equals(partnerOrganisation.getId()));

        return financeContactForOrganisation.map(existing -> COMPLETE).orElse(ACTION_REQUIRED);
    }

    protected ProjectActivityStates createProjectDetailsStatus(Project project) {
        return projectDetailsWorkflowHandler.isSubmitted(project) ? COMPLETE : ACTION_REQUIRED;
    }

    protected ProjectActivityStates createMonitoringOfficerStatus(final Optional<MonitoringOfficer> monitoringOfficer, final ProjectActivityStates leadProjectDetailsSubmitted) {
        if (leadProjectDetailsSubmitted.equals(COMPLETE)) {
            return monitoringOfficer.isPresent() ? COMPLETE : PENDING;
        } else {
            return NOT_STARTED;
        }

    }

    protected ProjectActivityStates createBankDetailStatus(Long projectId, Long applicationId, Long organisationId, final Optional<BankDetails> bankDetails, ProjectActivityStates financeContactStatus) {
        if (bankDetails.isPresent()) {
            return bankDetails.get().isApproved() ? COMPLETE : PENDING;
        } else {
            Optional<Boolean> result = financeRowService.organisationSeeksFunding(projectId, applicationId, organisationId).getOptionalSuccessObject();

            boolean seeksFunding = result.map(Boolean::booleanValue).orElse(false);

            if (!seeksFunding) {
                return NOT_REQUIRED;
            } else {
                if (COMPLETE.equals(financeContactStatus)) {
                    return ACTION_REQUIRED;
                } else {
                    return NOT_STARTED;
                }
            }
        }
    }

    protected ProjectActivityStates createFinanceCheckStatus(final Project project, final Organisation organisation, boolean isAwaitingResponse) {
        PartnerOrganisation partnerOrg = partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(project.getId(), organisation.getId());
            if (financeChecksApproved(partnerOrg)) {
                return COMPLETE;
            }
            if (isAwaitingResponse) {
                return ACTION_REQUIRED;
            }
            return PENDING;
    }

    private boolean financeChecksApproved(PartnerOrganisation partnerOrg) {
        return asList(EligibilityState.APPROVED, EligibilityState.NOT_APPLICABLE).contains(eligibilityWorkflowHandler.getState(partnerOrg)) &&
                asList(ViabilityState.APPROVED, ViabilityState.NOT_APPLICABLE).contains(viabilityWorkflowHandler.getState(partnerOrg));
    }

    protected ProjectActivityStates createLeadSpendProfileStatus(final Project project, final ProjectActivityStates spendProfileStatus, final Optional<SpendProfile> spendProfile) {
        ProjectActivityStates state = spendProfileStatus;

        if (spendProfileStatus == COMPLETE || spendProfileStatus == PENDING) {
            if (spendProfile.get().getApproval().equals(ApprovalType.REJECTED) || project.getSpendProfileSubmittedDate() == null) {
                state = ACTION_REQUIRED;
            } else if (project.getSpendProfileSubmittedDate() != null && !spendProfile.get().getApproval().equals(ApprovalType.APPROVED)) {
                state = PENDING;
            }
        }
        return state;
    }

    protected ProjectActivityStates createSpendProfileStatus(final ProjectActivityStates financeCheckStatus, final Optional<SpendProfile> spendProfile) {
        if (spendProfile != null && spendProfile.isPresent() && financeCheckStatus.equals(COMPLETE)) {
            if (spendProfile.get().isMarkedAsComplete()) {
                return COMPLETE;
            } else {
                return ACTION_REQUIRED;
            }
        }
        return NOT_STARTED;
    }

    protected ProjectActivityStates createGrantOfferLetterStatus(final ProjectActivityStates leadSpendProfileState,
                                                                 final ProjectActivityStates otherDocumentsState,
                                                                 final Project project,
                                                                 final boolean isLeadPartner) {
        ProjectActivityStates golState = NOT_REQUIRED;
        if (COMPLETE.equals(leadSpendProfileState) && COMPLETE.equals(otherDocumentsState)) {
            golState = isLeadPartner ? PENDING : NOT_REQUIRED;
            if (golWorkflowHandler.isAlreadySent(project)) {
                golState = isLeadPartner ? ACTION_REQUIRED : PENDING;
                if (golWorkflowHandler.isReadyToApprove(project)) {
                    golState = PENDING;
                }
                if (golWorkflowHandler.isApproved(project)) {
                    golState = COMPLETE;
                }
            }
        }
        return golState;
    }

    protected ServiceResult<ProjectUser> getCurrentlyLoggedInPartner(Project project) {
        return getCurrentlyLoggedInProjectUser(project, PROJECT_PARTNER);
    }

    protected ServiceResult<ProjectUser> getCurrentlyLoggedInProjectUser(Project project, ProjectParticipantRole role) {

        return getCurrentlyLoggedInUser().andOnSuccess(currentUser ->
                simpleFindFirst(project.getProjectUsers(), pu -> findUserAndRole(role, currentUser, pu)).
                        map(user -> serviceSuccess(user)).
                        orElse(serviceFailure(forbiddenError())));
    }

    private boolean findUserAndRole(ProjectParticipantRole role, User currentUser, ProjectUser pu) {
        return pu.getUser().getId().equals(currentUser.getId()) && pu.getRole().equals(role);
    }
}
