package com.worth.ifs.finance.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.finance.domain.CostField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * CostFieldRestServiceImpl is a utility for CRUD operations on {@link CostField}.
 * This class connects to the {@link com.worth.ifs.finance.controller.CostFieldController}
 * through a REST call.
 */
@Service
public class CostFieldRestServiceImpl extends BaseRestService implements CostFieldRestService {
    @Value("${ifs.data.service.rest.costfield}")
    String costFieldRestURL;

    public List<CostField> getCostFields() {
        return asList(restGet(costFieldRestURL + "/findAll/", CostField[].class));
    }
}
