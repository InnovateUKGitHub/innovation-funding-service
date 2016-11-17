package com.worth.ifs.project.transactional;

import com.worth.ifs.project.bankdetails.domain.BankDetails;
import com.worth.ifs.project.bankdetails.repository.BankDetailsRepository;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.transactional.FinanceRowService;
import com.worth.ifs.invite.domain.ProjectParticipantRole;
import com.worth.ifs.project.constant.ProjectActivityStates;
import com.worth.ifs.project.domain.MonitoringOfficer;
import com.worth.ifs.project.domain.PartnerOrganisation;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.finance.domain.SpendProfile;
import com.worth.ifs.project.finance.repository.SpendProfileRepository;
import com.worth.ifs.project.finance.workflow.financechecks.configuration.FinanceCheckWorkflowHandler;
import com.worth.ifs.project.mapper.ProjectMapper;
import com.worth.ifs.project.mapper.ProjectUserMapper;
import com.worth.ifs.project.repository.MonitoringOfficerRepository;
import com.worth.ifs.project.repository.PartnerOrganisationRepository;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.repository.ProjectUserRepository;
import com.worth.ifs.project.resource.ApprovalType;
import com.worth.ifs.project.workflow.projectdetails.configuration.ProjectDetailsWorkflowHandler;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.commons.error.CommonErrors.forbiddenError;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static com.worth.ifs.project.constant.ProjectActivityStates.*;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.util.Arrays.asList;

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
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;

    @Autowired
    private FinanceCheckWorkflowHandler financeCheckWorkflowHandler;

    @Autowired
    protected PartnerOrganisationRepository partnerOrganisationRepository;

    List<ProjectUser> getProjectUsersByProjectId(Long projectId) {
        return projectUserRepository.findByProjectId(projectId);
    }

    protected ProjectActivityStates createOtherDocumentStatus(final Project project) {

        if (project.getOtherDocumentsApproved() != null && project.getOtherDocumentsApproved()) {
            return COMPLETE;
        }

        if (project.getOtherDocumentsApproved() != null && !project.getOtherDocumentsApproved()) {
            return PENDING;
        }

        if (project.getOtherDocumentsApproved() == null && project.getDocumentsSubmittedDate() != null) {
            return ACTION_REQUIRED;
        }

        return PENDING;
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
            return monitoringOfficer.isPresent() ? COMPLETE : ACTION_REQUIRED;
        } else {
            return NOT_STARTED;
        }

    }

    protected ProjectActivityStates createBankDetailStatus(final Optional<BankDetails> bankDetails, ProjectActivityStates financeContactStatus) {
        if (bankDetails.isPresent()) {
            return bankDetails.get().isApproved() ? COMPLETE : PENDING;
        } else {
            if (COMPLETE.equals(financeContactStatus)) {
                return ACTION_REQUIRED;
            } else {
                return NOT_STARTED;
            }
        }
    }

    protected ProjectActivityStates createFinanceCheckStatus(final Project project, final Organisation organisation, ProjectActivityStates bankDetailsStatus) {

        PartnerOrganisation partnerOrg = partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(project.getId(), organisation.getId());

        if (financeCheckWorkflowHandler.isApproved(partnerOrg)) {
            return COMPLETE;
        }

        if (asList(COMPLETE, PENDING, NOT_REQUIRED).contains(bankDetailsStatus)) {
            return ACTION_REQUIRED;
        } else {
            return NOT_STARTED;
        }
    }

    protected ProjectActivityStates createLeadSpendProfileStatus(final ProjectActivityStates financeCheckStatus, final Optional<SpendProfile> spendProfile) {
        //TODO - Implement REJECT status when internal spend profile action story is completed

        if (spendProfile.isPresent()) {
            if (spendProfile.get().isMarkedAsComplete() && (spendProfile.get().getApproval() == ApprovalType.APPROVED)) {
                return COMPLETE;
            } else {
                return ACTION_REQUIRED;
            }
        } else {
            if(financeCheckStatus.equals(COMPLETE)){
                return PENDING;
            } else {
                return NOT_STARTED;
            }
        }
    }

    protected ProjectActivityStates createSpendProfileStatus(final ProjectActivityStates financeCheckStatus, final Optional<SpendProfile> spendProfile) {
        //TODO - Implement REJECT status when internal spend profile action story is completed

        if (spendProfile.isPresent()) {
            if (spendProfile.get().isMarkedAsComplete()) {
                return COMPLETE;
            } else {
                return ACTION_REQUIRED;
            }
        } else {
            if(financeCheckStatus.equals(COMPLETE)){
                return PENDING;
            } else {
                return NOT_STARTED;
            }
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

    protected ServiceResult<PartnerOrganisation> getPartnerOrganisation(Long projectId, Long organisationId) {
        return find(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId),
                notFoundError(PartnerOrganisation.class, projectId, organisationId));
    }

    private boolean findUserAndRole(ProjectParticipantRole role, User currentUser, ProjectUser pu) {
        return pu.getUser().getId().equals(currentUser.getId()) && pu.getRole().equals(role);
    }
}
