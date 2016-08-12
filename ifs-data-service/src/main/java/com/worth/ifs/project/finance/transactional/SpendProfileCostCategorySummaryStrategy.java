package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;

import java.util.List;

/**
 * Represents a strategy for providing a set of Cost Category summaries for the generation of Spend Profiles per Partner
 * Organisation
 */
public interface SpendProfileCostCategorySummaryStrategy {

    ServiceResult<List<SpendProfileCostCategorySummary>> getCostCategorySummaries(Long projectId, Long organisationId);
}
