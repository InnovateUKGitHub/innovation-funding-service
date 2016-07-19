package com.worth.ifs.finance.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.transactional.CostService;
import com.worth.ifs.validator.util.ValidationUtil;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.finance.service.CostRestServiceImpl} and other REST-API users
 * to manage {@link Cost} related data.
 */
@RestController
@RequestMapping("/cost")
public class CostController {
    private static final Log LOG = LogFactory.getLog(CostController.class);

    @Autowired
    private CostService costService;

    @Autowired
    private ValidationUtil validationUtil;

    @RequestMapping("/add/{applicationFinanceId}/{questionId}")
    public RestResult<ValidationMessages> add(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId,
            @PathVariable("questionId") final Long questionId,
            @RequestBody(required=false) final CostItem newCostItem) {
    	RestResult<CostItem> createResult = costService.addCost(applicationFinanceId, questionId, newCostItem).toPostCreateResponse();
        if(createResult.isFailure()){
            return RestResult.restFailure(createResult.getFailure());
        }else{
            CostItem costItem = createResult.getSuccessObject();
            ValidationMessages validationMessages = validationUtil.validateCostItem(costItem);
            return RestResult.restSuccess(validationMessages, HttpStatus.CREATED);
        }
    }
    
    @RequestMapping("/add-without-persisting/{applicationFinanceId}/{questionId}")
    public RestResult<CostItem> addWithoutPersisting(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId,
            @PathVariable("questionId") final Long questionId) {
        return costService.addCostWithoutPersisting(applicationFinanceId, questionId).toPostCreateResponse();
    }

    @RequestMapping("/{id}")
    public RestResult<CostItem> get(@PathVariable("id") final Long id) {
        return costService.getCostItem(id).toGetResponse();
    }

    /**
     * Save the updated CostItem and if there are validation messages, return those (but still save)
     * @return ValidationMessages resource object to store validation messages about invalid user input.
     */
    @RequestMapping("/update/{id}")
    public RestResult<ValidationMessages> update(@PathVariable("id") final Long id, @RequestBody final CostItem newCostItem) {
        RestResult<CostItem> updateResult = costService.updateCost(id, newCostItem).toGetResponse();
        if(updateResult.isFailure()){
            return RestResult.restFailure(updateResult.getFailure());
        }else{
            CostItem costItem = updateResult.getSuccessObject();
            ValidationMessages validationMessages = validationUtil.validateCostItem(costItem);
            return RestResult.restSuccess(validationMessages);
        }
    }

    @RequestMapping("/delete/{costId}")
    public RestResult<Void> delete(@PathVariable("costId") final Long costId) {
        return costService.deleteCost(costId).toDeleteResponse();
    }
}
