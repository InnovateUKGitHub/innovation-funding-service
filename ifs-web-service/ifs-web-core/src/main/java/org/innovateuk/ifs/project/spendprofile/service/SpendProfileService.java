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

    @NotSecured("Not currently secured")
    ServiceResult<Void> generateSpendProfile(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<Void> approveOrRejectSpendProfile(Long projectId, ApprovalType approvalType);

    @NotSecured("Not currently secured")
    ApprovalType getSpendProfileStatusByProjectId(Long projectId);

    @NotSecured("Not currently secured")
    Optional<SpendProfileResource> getSpendProfile(Long projectId, Long organisationId);

    @NotSecured("Not currently secured")
    SpendProfileTableResource getSpendProfileTable(Long projectId, Long organisationId);

    @NotSecured("Not currently secured")
    SpendProfileCSVResource getSpendProfileCSV(Long projectId, Long organisationId);

    @NotSecured("Not currently secured")
    ServiceResult<Void> saveSpendProfile(Long projectId, Long organisationId, SpendProfileTableResource table);

    @NotSecured("Not currently secured")
    ServiceResult<Void> markSpendProfileComplete(Long projectId, Long organisationId);

    @NotSecured("Not currently secured")
    ServiceResult<Void> markSpendProfileIncomplete(Long projectId, Long organisationId);

    @NotSecured("Not currently secured")
    ServiceResult<Void> completeSpendProfilesReview(Long projectId);

}
