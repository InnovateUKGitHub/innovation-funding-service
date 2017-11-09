package org.innovateuk.ifs.project.spendprofile.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;

import java.util.Optional;

/**
 * A service for dealing with a Project's finance operations
 */
public interface SpendProfileService {

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> generateSpendProfile(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> approveOrRejectSpendProfile(Long projectId, ApprovalType approvalType);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ApprovalType getSpendProfileStatusByProjectId(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<SpendProfileResource> getSpendProfile(Long projectId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    SpendProfileTableResource getSpendProfileTable(Long projectId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    SpendProfileCSVResource getSpendProfileCSV(Long projectId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> saveSpendProfile(Long projectId, Long organisationId, SpendProfileTableResource table);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> markSpendProfileComplete(Long projectId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> markSpendProfileIncomplete(Long projectId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> completeSpendProfilesReview(Long projectId);

}
