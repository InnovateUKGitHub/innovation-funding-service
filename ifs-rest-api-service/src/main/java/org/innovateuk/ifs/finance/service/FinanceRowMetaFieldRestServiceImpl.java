package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.financeRowMetaFieldResourceListType;

/**
 * FinanceRowMetaFieldRestServiceImpl is a utility for CRUD operations on {@link FinanceRowMetaFieldResource}.
 * This class connects to the
 * through a REST call.
 */
@Service
public class FinanceRowMetaFieldRestServiceImpl extends BaseRestService implements FinanceRowMetaFieldRestService {

    private String costFieldRestURL = "/costfield";

    @Override
    public RestResult<List<FinanceRowMetaFieldResource>> getFinanceRowMetaFields() {
        return getWithRestResult(costFieldRestURL + "/findAll/", financeRowMetaFieldResourceListType());
    }
}
