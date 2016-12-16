package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaValueResource;

public interface FinanceRowMetaValueRestService {

    RestResult<FinanceRowMetaValueResource> findOne(Long id);
}
