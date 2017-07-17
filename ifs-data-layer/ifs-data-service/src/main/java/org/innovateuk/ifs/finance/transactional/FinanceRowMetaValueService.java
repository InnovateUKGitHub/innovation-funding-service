package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaValueResource;
import org.springframework.security.access.prepost.PostAuthorize;

public interface FinanceRowMetaValueService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<FinanceRowMetaValueResource> findOne(Long id);
}
