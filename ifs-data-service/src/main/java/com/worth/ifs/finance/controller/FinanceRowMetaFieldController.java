package com.worth.ifs.finance.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.finance.domain.FinanceRowMetaField;
import com.worth.ifs.finance.resource.FinanceRowMetaFieldResource;
import com.worth.ifs.finance.transactional.FinanceRowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.finance.service.FinanceRowMetaFieldRestServiceImpl} and other REST-API users
 * to manage {@link FinanceRowMetaField} related data.
 */
@RestController
@RequestMapping("/costfield")
public class FinanceRowMetaFieldController {

    @Autowired
    private FinanceRowService costFieldService;

    @RequestMapping("/findAll/")
    public RestResult<List<FinanceRowMetaFieldResource>> findAll() {
        return costFieldService.findAllCostFields().toGetResponse();
    }
}
