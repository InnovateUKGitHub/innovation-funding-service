package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.mapper.PartnerOrganisationMapper;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.PendingPartnerProgressRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
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
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
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
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private RemovePartnerNotificationService removePartnerNotificationService;

    enum Notifications {
        REMOVE_PROJECT_ORGANISATION
    }

    @Override
    public ServiceResult<List<PartnerOrganisationResource>> getProjectPartnerOrganisations(Long projectId) {
        return find(partnerOrganisationRepository.findByProjectId(projectId),
                notFoundError(PartnerOrganisation.class, id)).
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
    public ServiceResult<Void> removePartnerOrganisation(long projectId, long organisationId) {
        return find(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId),
                notFoundError(PartnerOrganisation.class, id)).andOnSuccessReturnVoid(
                projectPartner -> validatePartnerNotLead(projectPartner).andOnSuccess(
                        () -> {
                            removePartnerOrg(projectId, projectPartner.getOrganisation().getId());
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
        projectUserRepository.deleteAllByProjectIdAndOrganisationId(projectId, organisationId);
        partnerOrganisationRepository.deleteOneByProjectIdAndOrganisationId(projectId, organisationId);
        Optional<PendingPartnerProgress> pendingPartnerProgress = pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(organisationId, projectId);
        if (pendingPartnerProgress.isPresent()) {
            pendingPartnerProgressRepository.deleteById(pendingPartnerProgress.get().getId());
        }
        deleteProjectFinance(projectId, organisationId);
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
