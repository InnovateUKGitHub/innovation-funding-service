package org.innovateuk.ifs.finance;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.*;

import java.util.List;

/**
 * A service for dealing with a Project's finance operations
 */
public interface ProjectFinanceService {

    List<ProjectFinanceResource> getProjectFinances(Long projectId);

    ViabilityResource getViability(Long projectId, Long organisationId);

    ServiceResult<Void> saveViability(Long projectId, Long organisationId, ViabilityState viability, ViabilityRagStatus viabilityRagRating);

    EligibilityResource getEligibility(Long projectId, Long organisationId);

    ServiceResult<Void> saveEligibility(Long projectId, Long organisationId, EligibilityState eligibility, EligibilityRagStatus eligibilityRagStatus);

    boolean isCreditReportConfirmed(Long projectId, Long organisationId);

    ServiceResult<Void> saveCreditReportConfirmed(Long projectId, Long organisationId, boolean confirmed);

    ProjectFinanceResource getProjectFinance(Long projectId, Long organisationId);

}
