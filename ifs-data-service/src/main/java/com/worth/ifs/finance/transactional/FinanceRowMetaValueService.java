package com.worth.ifs.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.resource.FinanceRowMetaValueId;
import com.worth.ifs.finance.resource.FinanceRowMetaValueResource;
import org.springframework.security.access.prepost.PostAuthorize;

public interface FinanceRowMetaValueService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<FinanceRowMetaValueResource> findOne(FinanceRowMetaValueId id);
}