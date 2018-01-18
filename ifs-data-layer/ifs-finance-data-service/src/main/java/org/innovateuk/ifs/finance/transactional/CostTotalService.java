package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;

public interface CostTotalService {

    @SecuredBySpring(value = "TODO", description = "TODO")
    ServiceResult<Void> saveCostTotal(FinanceCostTotalResource costTotalResource);
}
