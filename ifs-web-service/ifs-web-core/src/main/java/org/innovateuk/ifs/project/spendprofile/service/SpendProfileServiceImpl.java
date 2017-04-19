package org.innovateuk.ifs.project.spendprofile.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.resource.SpendProfileResource;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A service for dealing with a Project's spend profile operations
 */
@Service
public class SpendProfileServiceImpl implements SpendProfileService {
    @Autowired
    private SpendProfileRestService spendProfileRestService;

    @Override
    public ServiceResult<Void> generateSpendProfile(Long projectId) {
        return spendProfileRestService.generateSpendProfile(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> approveOrRejectSpendProfile(Long projectId, ApprovalType approvalType) {
        return spendProfileRestService.acceptOrRejectSpendProfile(projectId, approvalType).toServiceResult();
    }

    @Override
    public ApprovalType getSpendProfileStatusByProjectId(Long projectId) {
        return spendProfileRestService.getSpendProfileStatusByProjectId(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public SpendProfileTableResource getSpendProfileTable(Long projectId, Long organisationId) {
        return spendProfileRestService.getSpendProfileTable(projectId, organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public SpendProfileCSVResource getSpendProfileCSV(Long projectId, Long organisationId) {
        return spendProfileRestService.getSpendProfileCSV(projectId, organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<SpendProfileResource> getSpendProfile(Long projectId, Long organisationId) {
        return spendProfileRestService.getSpendProfile(projectId, organisationId).toOptionalIfNotFound().getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> saveSpendProfile(Long projectId, Long organisationId, SpendProfileTableResource table) {
        return spendProfileRestService.saveSpendProfile(projectId, organisationId, table).toServiceResult();
    }

    @Override
    public ServiceResult<Void> markSpendProfileComplete(Long projectId, Long organisationId) {
        return spendProfileRestService.markSpendProfileComplete(projectId, organisationId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> markSpendProfileIncomplete(Long projectId, Long organisationId) {
        return spendProfileRestService.markSpendProfileIncomplete(projectId, organisationId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> completeSpendProfilesReview(Long projectId) {
        return spendProfileRestService.completeSpendProfilesReview(projectId).toServiceResult();
    }
}
