package com.worth.ifs.finance.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.transactional.CostFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.finance.service.CostFieldRestServiceImpl} and other REST-API users
 * to manage {@link CostField} related data.
 */
@RestController
@RequestMapping("/costfield")
public class CostFieldController {

    @Autowired
    private CostFieldService costFieldService;

    @RequestMapping("/findAll/")
    public RestResult<List<CostFieldResource>> findAll() {
        return newRestHandler().perform(() -> costFieldService.findAll());
    }
}
