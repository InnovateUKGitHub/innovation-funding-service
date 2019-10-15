package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.PendingPartnerProgress;
import org.innovateuk.ifs.project.core.mapper.PendingPartnerProgressMapper;
import org.innovateuk.ifs.project.core.repository.PendingPartnerProgressRepository;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PARTNER_NOT_READY_TO_JOIN_PROJECT;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class PendingPartnerProgressServiceImpl extends RootTransactionalService implements PendingPartnerProgressService {

    @Autowired
    private PendingPartnerProgressMapper pendingPartnerProgressMapper;

    @Autowired
    private PendingPartnerProgressRepository pendingPartnerProgressRepository;

    @Override
    public ServiceResult<PendingPartnerProgressResource> getPendingPartnerProgress(long projectId, long organisationId) {
        return getPartnerProgress(projectId, organisationId)
                .andOnSuccessReturn(pendingPartnerProgressMapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markYourOrganisationComplete(long projectId, long organisationId) {
        return getPartnerProgress(projectId, organisationId)
                .andOnSuccessReturnVoid(PendingPartnerProgress::markYourOrganisationComplete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markYourFundingComplete(long projectId, long organisationId) {
        return getPartnerProgress(projectId, organisationId)
                .andOnSuccessReturnVoid(PendingPartnerProgress::markYourFundingComplete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markTermsAndConditionsComplete(long projectId, long organisationId) {
        return getPartnerProgress(projectId, organisationId)
                .andOnSuccessReturnVoid(PendingPartnerProgress::markTermsAndConditionsComplete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markYourOrganisationIncomplete(long projectId, long organisationId) {
        return getPartnerProgress(projectId, organisationId)
                .andOnSuccessReturnVoid(PendingPartnerProgress::markYourOrganisationIncomplete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markYourFundingIncomplete(long projectId, long organisationId) {
        return getPartnerProgress(projectId, organisationId)
                .andOnSuccessReturnVoid(PendingPartnerProgress::markYourFundingIncomplete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markTermsAndConditionsIncomplete(long projectId, long organisationId) {
        return getPartnerProgress(projectId, organisationId)
                .andOnSuccessReturnVoid(PendingPartnerProgress::markTermsAndConditionsIncomplete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> completePartnerSetup(long projectId, long organisationId) {
        return getPartnerProgress(projectId, organisationId)
                .andOnSuccess(this::isReadyToJoinProject)
                .andOnSuccessReturnVoid(pendingPartnerProgressRepository::delete);
    }

    private ServiceResult<PendingPartnerProgress> isReadyToJoinProject(PendingPartnerProgress progress) {
        if (progress.isReadyToJoinProject()) {
            return serviceSuccess(progress);
        }
        return serviceFailure(PARTNER_NOT_READY_TO_JOIN_PROJECT);
    }

    private ServiceResult<PendingPartnerProgress> getPartnerProgress(long projectId, long organisationId) {
        return find(pendingPartnerProgressRepository.findByPartnerOrganisationProjectIdAndPartnerOrganisationOrganisationId(projectId, organisationId),
                notFoundError(PendingPartnerProgress.class, projectId, organisationId));
    }
}
