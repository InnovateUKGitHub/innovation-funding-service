package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.spendprofile.domain.CostCategoryType;

/**
 * Represents a component that is able to use some strategy to determine which CostCategoryType to use for a given set of
 * Finances
 */
public interface CostCategoryTypeStrategy {

    ServiceResult<CostCategoryType> getOrCreateCostCategoryTypeForSpendProfile(Long projectId, Long organisationId);
}
