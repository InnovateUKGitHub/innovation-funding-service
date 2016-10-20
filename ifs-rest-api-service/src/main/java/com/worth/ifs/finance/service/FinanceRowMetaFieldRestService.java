package com.worth.ifs.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.finance.domain.FinanceRowMetaField;
import com.worth.ifs.finance.resource.FinanceRowMetaFieldResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link FinanceRowMetaField} related data.
 */
public interface FinanceRowMetaFieldRestService {

    RestResult<List<FinanceRowMetaFieldResource>> getFinanceRowMetaFields();
}
