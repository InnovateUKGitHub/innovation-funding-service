package org.innovateuk.ifs.project.finance;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.Eligibility;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.EligibilityStatus;
import org.innovateuk.ifs.project.finance.resource.Viability;
import org.innovateuk.ifs.project.finance.resource.ViabilityResource;
import org.innovateuk.ifs.project.finance.resource.ViabilityStatus;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.resource.SpendProfileResource;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;

import java.util.List;
import java.util.Optional;

/**
 * A service for dealing with a Project's finance operations
 */
public interface ProjectFinanceService {

    ServiceResult<Void> generateSpendProfile(Long projectId);

    ServiceResult<Void> approveOrRejectSpendProfile(Long projectId, ApprovalType approvalType);

    ApprovalType getSpendProfileStatusByProjectId(Long projectId);

    Optional<SpendProfileResource> getSpendProfile(Long projectId, Long organisationId);

    SpendProfileTableResource getSpendProfileTable(Long projectId, Long organisationId);

    SpendProfileCSVResource getSpendProfileCSV(Long projectId, Long organisationId);

    ServiceResult<Void> saveSpendProfile(Long projectId, Long organisationId, SpendProfileTableResource table);

    ServiceResult<Void> markSpendProfileComplete(Long projectId, Long organisationId);

    ServiceResult<Void> markSpendProfileIncomplete(Long projectId, Long organisationId);

    ServiceResult<Void> completeSpendProfilesReview(Long projectId);

    List<ProjectFinanceResource> getProjectFinances(Long projectId);

    ViabilityResource getViability(Long projectId, Long organisationId);

    ServiceResult<Void> saveViability(Long projectId, Long organisationId, Viability viability, ViabilityStatus viabilityRagRating);

    EligibilityResource getEligibility(Long projectId, Long organisationId);

    ServiceResult<Void> saveEligibility(Long projectId, Long organisationId, Eligibility eligibility, EligibilityStatus eligibilityStatus);

    boolean isCreditReportConfirmed(Long projectId, Long organisationId);

    ServiceResult<Void> saveCreditReportConfirmed(Long projectId, Long organisationId, boolean confirmed);
}
