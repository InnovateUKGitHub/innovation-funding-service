package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.mapper.PartnerOrganisationMapper;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.PendingPartnerProgressRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.invite.repository.ProjectPartnerInviteRepository;
import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.CANNOT_REMOVE_LEAD_ORGANISATION_FROM_PROJECT;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
@Transactional(readOnly = true)
public class PartnerOrganisationServiceImpl implements PartnerOrganisationService {

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    private PartnerOrganisationMapper partnerOrganisationMapper;

    @Autowired
    private ProjectUserInviteRepository projectUserInviteRepository;

    @Autowired
    private PendingPartnerProgressRepository pendingPartnerProgressRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private ProjectPartnerChangeService projectPartnerChangeService;

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @Autowired
    private ProjectPartnerInviteRepository projectPartnerInviteRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private RemovePartnerNotificationService removePartnerNotificationService;

    @Override
    public ServiceResult<List<PartnerOrganisationResource>> getProjectPartnerOrganisations(Long projectId) {
        return find(partnerOrganisationRepository.findByProjectId(projectId),
                notFoundError(PartnerOrganisation.class)).
                andOnSuccessReturn(lst -> simpleMap(lst, partnerOrganisationMapper::mapToResource));
    }

    @Override
    public ServiceResult<PartnerOrganisationResource> getPartnerOrganisation(Long projectId, Long organisationId) {
        return find(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId),
                notFoundError(PartnerOrganisation.class)).
                andOnSuccessReturn(partnerOrganisationMapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> removePartnerOrganisation(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return find(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectOrganisationCompositeId.getProjectId(), projectOrganisationCompositeId.getOrganisationId()),
                notFoundError(PartnerOrganisation.class)).andOnSuccess(
                projectPartner -> validatePartnerNotLead(projectPartner).andOnSuccessReturnVoid(
                        () -> {
                            removePartnerOrg(projectOrganisationCompositeId.getProjectId(), projectPartner.getOrganisation().getId());
                            projectPartnerChangeService.updateProjectWhenPartnersChange(projectOrganisationCompositeId.getProjectId());
                            removePartnerNotificationService.sendNotifications(projectPartner.getProject(), projectPartner.getOrganisation());
                        })
        );
    }

    private ServiceResult<Void> validatePartnerNotLead(PartnerOrganisation partnerOrganisation) {
        return partnerOrganisation.isLeadOrganisation() ?
                serviceFailure(CANNOT_REMOVE_LEAD_ORGANISATION_FROM_PROJECT) :
                serviceSuccess();
    }

    private void removePartnerOrg(long projectId, long organisationId) {
        projectUserInviteRepository.deleteAllByProjectIdAndOrganisationId(projectId, organisationId);
        projectPartnerInviteRepository.deleteByProjectIdAndInviteOrganisationOrganisationId(projectId, organisationId);
        projectUserRepository.deleteAllByProjectIdAndOrganisationId(projectId, organisationId);
        partnerOrganisationRepository.deleteOneByProjectIdAndOrganisationId(projectId, organisationId);
        Optional<PendingPartnerProgress> pendingPartnerProgress = pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(organisationId, projectId);
        if (pendingPartnerProgress.isPresent()) {
            pendingPartnerProgressRepository.deleteById(pendingPartnerProgress.get().getId());
        }
        deleteProjectFinance(projectId, organisationId);
        deleteBankDetails(projectId, organisationId);
    }

    private void deleteBankDetails(long projectId, long organisationId) {
        Optional<BankDetails> bankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId);
        if (bankDetails.isPresent()) {
            bankDetailsRepository.delete(bankDetails.get());
        }
    }

    private void deleteProjectFinance(long projectId, long organisationId) {
        find(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId),
                notFoundError(ProjectFinance.class)).andOnSuccessReturnVoid(projectFinance -> {
            deleteThreads(projectFinance.getId());
            projectFinanceRowRepository.deleteAllByTargetId(projectFinance.getId());
            projectFinanceRepository.deleteAllByProjectIdAndOrganisationId(projectId, organisationId);
        });
    }

    private void deleteThreads(long projectFinanceId) {
        noteRepository.deleteAllByClassPk(projectFinanceId);
        queryRepository.deleteAllByClassPk(projectFinanceId);
    }
}