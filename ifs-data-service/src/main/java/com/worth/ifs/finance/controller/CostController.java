package com.worth.ifs.finance.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.transactional.CostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.finance.service.CostRestServiceImpl} and other REST-API users
 * to manage {@link Cost} related data.
 */
@RestController
@RequestMapping("/cost")
public class CostController {

    @Autowired
    private CostService costService;

    @RequestMapping("/add/{applicationFinanceId}/{questionId}")
    public RestResult<CostItem> add(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId,
            @PathVariable("questionId") final Long questionId,
            @RequestBody(required=false) final CostItem newCostItem) {
        return newRestHandler().perform(() -> costService.add(applicationFinanceId, questionId, newCostItem));
    }

    @RequestMapping("/update/{id}")
    public RestResult<Void> update(@PathVariable("id") final Long id, @RequestBody final CostItem newCostItem) {
        return newRestHandler().perform(() -> costService.update(id, newCostItem));
    }

    @RequestMapping("/get/{applicationFinanceId}")
    public RestResult<List<Cost>> findByApplicationId(@PathVariable("applicationFinanceId") final Long applicationFinanceId) {
        return newRestHandler().perform(() -> costService.findByApplicationId(applicationFinanceId));
    }

    @RequestMapping("/findById/{id}")
    public RestResult<Cost> findById(@PathVariable("id") final Long id) {
        return newRestHandler().perform(() -> costService.findById(id));
    }

    @RequestMapping("/delete/{costId}")
    public RestResult<Void> delete(@PathVariable("costId") final Long costId) {
        return newRestHandler().perform(() -> costService.delete(costId));
    }
}
