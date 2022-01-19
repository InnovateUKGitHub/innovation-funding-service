package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link FinanceRowMetaFieldResource} related data.
 */
public interface FinanceRowMetaFieldRestService {

    RestResult<List<FinanceRowMetaFieldResource>> getFinanceRowMetaFields();
}
