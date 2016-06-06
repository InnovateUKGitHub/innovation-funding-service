package com.worth.ifs.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.CostFieldResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.costFieldResourceListType;

/**
 * CostFieldRestServiceImpl is a utility for CRUD operations on {@link CostField}.
 * This class connects to the {@link com.worth.ifs.finance.controller.CostFieldController}
 * through a REST call.
 */
@Service
public class CostFieldRestServiceImpl extends BaseRestService implements CostFieldRestService {

    private String costFieldRestURL = "/costfield";

    @Override
    public RestResult<List<CostFieldResource>> getCostFields() {
        return getWithRestResult(costFieldRestURL + "/findAll/", costFieldResourceListType());
    }
}
