package org.innovateuk.ifs.project.financecheck.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;

/**
 * Represents a strategy for providing a set of Cost Category summaries for the generation of Spend Profiles per Partner
 * Organisation
 */
public interface SpendProfileCostCategorySummaryStrategy {

    ServiceResult<SpendProfileCostCategorySummaries> getCostCategorySummaries(Long projectId, Long organisationId);
}
