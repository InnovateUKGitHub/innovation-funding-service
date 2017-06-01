package org.innovateuk.ifs.project.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.invite.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.mapper.ProjectMapper;
import org.innovateuk.ifs.project.mapper.ProjectUserMapper;
import org.innovateuk.ifs.project.monitoringofficer.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoringofficer.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState.SENT;
import static org.innovateuk.ifs.project.resource.ApprovalType.APPROVED;
import static org.innovateuk.ifs.project.resource.ApprovalType.UNSET;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

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
    private GrantOfferLetterWorkflowHandler golWorkflowHandler;

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
        if (APPROVED.equals(project.getOtherDocumentsApproved())) {
            return COMPLETE;
        } else if (UNSET.equals(project.getOtherDocumentsApproved()) && project.getDocumentsSubmittedDate() != null) {
            return PENDING;
        } else {
            return ACTION_REQUIRED;
        }
    }

    protected ProjectActivityStates createFinanceContactStatus(Project project, Organisation partnerOrganisation) {
        return getFinanceContact(project, partnerOrganisation).map(existing -> COMPLETE).orElse(ACTION_REQUIRED);
    }

    protected Optional<ProjectUser> getFinanceContact(final Project project, final Organisation organisation) {
        return simpleFindFirst(project.getProjectUsers(), pu -> pu.getRole().isFinanceContact()
                && pu.getOrganisation().getId().equals(organisation.getId()));
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
        } else if (!isSeekingFunding(projectId, applicationId, organisationId)) {
            return NOT_REQUIRED;
        } else if (COMPLETE.equals(financeContactStatus)) {
            return ACTION_REQUIRED;
        } else {
            return NOT_STARTED;
        }
    }

    private boolean isSeekingFunding(Long projectId, Long applicationId, Long organisationId) {
        return financeRowService.organisationSeeksFunding(projectId, applicationId, organisationId)
                .getOptionalSuccessObject()
                .map(Boolean::booleanValue)
                .orElse(false);
    }

    protected ProjectActivityStates createFinanceCheckStatus(final Project project, final Organisation organisation, boolean isAwaitingResponse) {
        PartnerOrganisation partnerOrg = partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(project.getId(), organisation.getId());
            if (financeChecksApproved(partnerOrg)) {
                return COMPLETE;
            } else if (isAwaitingResponse) {
                return ACTION_REQUIRED;
            } else {
                return PENDING;
            }
    }

    private boolean financeChecksApproved(PartnerOrganisation partnerOrg) {
        return asList(EligibilityState.APPROVED, EligibilityState.NOT_APPLICABLE).contains(eligibilityWorkflowHandler.getState(partnerOrg)) &&
                asList(ViabilityState.APPROVED, ViabilityState.NOT_APPLICABLE).contains(viabilityWorkflowHandler.getState(partnerOrg));
    }

    protected ProjectActivityStates createLeadSpendProfileStatus(final Project project, final  ProjectActivityStates financeCheckStatus, final Optional<SpendProfile> spendProfile) {
        ProjectActivityStates spendProfileStatus = createSpendProfileStatus(financeCheckStatus, spendProfile);
        if (COMPLETE.equals(spendProfileStatus) && !APPROVED.equals(spendProfile.get().getApproval())) {
            return project.getSpendProfileSubmittedDate() != null ? PENDING : ACTION_REQUIRED;
        } else {
            return spendProfileStatus;
        }
    }

    protected ProjectActivityStates createSpendProfileStatus(final ProjectActivityStates financeCheckStatus, final Optional<SpendProfile> spendProfile) {
        if (!spendProfile.isPresent()) {
            return NOT_STARTED;
        } else if (financeCheckStatus.equals(COMPLETE) && spendProfile.get().isMarkedAsComplete()) {
            return COMPLETE;
        } else {
            return ACTION_REQUIRED;
        }
    }

    protected ProjectActivityStates createLeadGrantOfferLetterStatus(final Project project) {
        GrantOfferLetterState state = golWorkflowHandler.getState(project);
        if (SENT.equals(state)) {
             return ACTION_REQUIRED;
        } else if (GrantOfferLetterState.PENDING.equals(state) && project.getGrantOfferLetter() != null) {
             return PENDING;
        } else {
            return createGrantOfferLetterStatus(project);
        }
    }

    protected ProjectActivityStates createGrantOfferLetterStatus(final Project project) {
        GrantOfferLetterState state = golWorkflowHandler.getState(project);
        if (GrantOfferLetterState.APPROVED.equals(state)) {
            return COMPLETE;
        } else if (GrantOfferLetterState.PENDING.equals(state)){
            return NOT_REQUIRED;
        } else {
            return PENDING;
        }
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
