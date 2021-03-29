package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;
import org.innovateuk.ifs.project.projectteam.mapper.PendingPartnerProgressMapper;
import org.innovateuk.ifs.project.core.repository.PendingPartnerProgressRepository;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PARTNER_ALREADY_TO_JOINED_PROJECT;
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

    @Autowired
    private PendingPartnerNotificationService pendingPartnerNotificationService;

    @Override
    public ServiceResult<PendingPartnerProgressResource> getPendingPartnerProgress(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return getPartnerProgress(projectOrganisationCompositeId)
                .andOnSuccessReturn(pendingPartnerProgressMapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markYourOrganisationComplete(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return getPartnerProgress(projectOrganisationCompositeId)
                .andOnSuccessReturnVoid(PendingPartnerProgress::markYourOrganisationComplete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markYourFundingComplete(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return getPartnerProgress(projectOrganisationCompositeId)
                .andOnSuccessReturnVoid(PendingPartnerProgress::markYourFundingComplete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markTermsAndConditionsComplete(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return getPartnerProgress(projectOrganisationCompositeId)
                .andOnSuccessReturnVoid(PendingPartnerProgress::markTermsAndConditionsComplete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markYourOrganisationIncomplete(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return getPartnerProgress(projectOrganisationCompositeId)
                .andOnSuccessReturnVoid(PendingPartnerProgress::markYourOrganisationIncomplete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markYourFundingIncomplete(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return getPartnerProgress(projectOrganisationCompositeId)
                .andOnSuccessReturnVoid(PendingPartnerProgress::markYourFundingIncomplete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markTermsAndConditionsIncomplete(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return getPartnerProgress(projectOrganisationCompositeId)
                .andOnSuccessReturnVoid(PendingPartnerProgress::markTermsAndConditionsIncomplete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markSubsidyBasisIncomplete(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return getPartnerProgress(projectOrganisationCompositeId)
                .andOnSuccessReturnVoid(PendingPartnerProgress::markSubsidyBasisIncomplete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markSubsidyBasisComplete(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return getPartnerProgress(projectOrganisationCompositeId)
                .andOnSuccessReturnVoid(PendingPartnerProgress::markSubsidyBasisComplete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> completePartnerSetup(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return getPartnerProgress(projectOrganisationCompositeId)
                .andOnSuccess(this::canJoinProject)
                .andOnSuccess(this::sendNotification)
                .andOnSuccessReturnVoid(PendingPartnerProgress::complete);
    }

    @Override
    @Transactional
    public ServiceResult<Void> resetPendingPartnerProgress(QuestionSetupType subsidyBasis, long projectId, long organisationId) {
        switch(subsidyBasis){
            case SUBSIDY_BASIS: {
                ProjectOrganisationCompositeId projectOrganisationCompositeId = ProjectOrganisationCompositeId.id(projectId, organisationId);
                return markYourFundingIncomplete(projectOrganisationCompositeId)
                        .andOnSuccess(() -> markYourFundingIncomplete(projectOrganisationCompositeId))
                        .andOnSuccess(() -> markTermsAndConditionsIncomplete(projectOrganisationCompositeId));
            }
            default:
                return serviceSuccess();

        }

    }

    private ServiceResult<PendingPartnerProgress> sendNotification(PendingPartnerProgress pendingPartnerProgress){
        pendingPartnerNotificationService.sendNotifications(pendingPartnerProgress.getPartnerOrganisation());
        return serviceSuccess(pendingPartnerProgress);
    }

    private ServiceResult<PendingPartnerProgress> canJoinProject(PendingPartnerProgress progress) {
        if (progress.isComplete()){
            return serviceFailure(PARTNER_ALREADY_TO_JOINED_PROJECT);
        } else if (!progress.isReadyToJoinProject()) {
            return serviceFailure(PARTNER_NOT_READY_TO_JOIN_PROJECT);
        } else {
            return serviceSuccess(progress);
        }
    }

    private ServiceResult<PendingPartnerProgress> getPartnerProgress(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return find(pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(projectOrganisationCompositeId.getProjectId(), projectOrganisationCompositeId.getOrganisationId()),
                notFoundError(PendingPartnerProgress.class, projectOrganisationCompositeId));
    }
}
