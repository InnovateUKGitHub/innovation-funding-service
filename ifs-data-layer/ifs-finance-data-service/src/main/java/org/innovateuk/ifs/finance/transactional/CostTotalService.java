package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;

import java.util.Collection;

public interface CostTotalService {

    @SecuredBySpring(value = "TODO", description = "TODO")
    ServiceResult<Void> saveCostTotal(FinanceCostTotalResource costTotalResource);

    @SecuredBySpring(value = "TODO", description = "TODO")
    ServiceResult<Void> saveCostTotals(Collection<FinanceCostTotalResource> costTotalResources);
}
