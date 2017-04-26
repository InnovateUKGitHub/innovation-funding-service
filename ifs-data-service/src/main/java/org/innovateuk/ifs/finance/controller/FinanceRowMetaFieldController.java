package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This RestController exposes CRUD operations to both the
 * {@link org.innovateuk.ifs.finance.service.FinanceRowMetaFieldRestServiceImpl} and other REST-API users
 * to manage {@link FinanceRowMetaField} related data.
 */
@RestController
@RequestMapping("/costfield")
public class FinanceRowMetaFieldController {

    @Autowired
    private FinanceRowService costFieldService;

    @GetMapping("/findAll/")
    public RestResult<List<FinanceRowMetaFieldResource>> findAll() {
        return costFieldService.findAllCostFields().toGetResponse();
    }
}
