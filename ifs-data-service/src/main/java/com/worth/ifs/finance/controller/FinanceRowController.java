package com.worth.ifs.finance.controller;

import com.worth.ifs.finance.resource.cost.FinanceRowItem;
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
import com.worth.ifs.finance.domain.FinanceRow;
import com.worth.ifs.finance.transactional.FinanceRowService;
import com.worth.ifs.validator.util.ValidationUtil;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.finance.service.FinanceRowRestServiceImpl} and other REST-API users
 * to manage {@link FinanceRow} related data.
 */
@RestController
@RequestMapping("/cost")
public class FinanceRowController {
    private static final Log LOG = LogFactory.getLog(FinanceRowController.class);

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private ValidationUtil validationUtil;

    @RequestMapping("/add/{applicationFinanceId}/{questionId}")
    public RestResult<ValidationMessages> add(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId,
            @PathVariable("questionId") final Long questionId,
            @RequestBody(required=false) final FinanceRowItem newCostItem) {
    	RestResult<FinanceRowItem> createResult = financeRowService.addCost(applicationFinanceId, questionId, newCostItem).toPostCreateResponse();
        if(createResult.isFailure()){
            return RestResult.restFailure(createResult.getFailure());
        }else{
            FinanceRowItem costItem = createResult.getSuccessObject();
            ValidationMessages validationMessages = validationUtil.validateCostItem(costItem);
            return RestResult.restSuccess(validationMessages, HttpStatus.CREATED);
        }
    }
    
    @RequestMapping("/add-without-persisting/{applicationFinanceId}/{questionId}")
    public RestResult<FinanceRowItem> addWithoutPersisting(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId,
            @PathVariable("questionId") final Long questionId) {
        return financeRowService.addCostWithoutPersisting(applicationFinanceId, questionId).toPostCreateResponse();
    }

    @RequestMapping("/{id}")
    public RestResult<FinanceRowItem> get(@PathVariable("id") final Long id) {
        return financeRowService.getCostItem(id).toGetResponse();
    }

    /**
     * Save the updated FinanceRowItem and if there are validation messages, return those (but still save)
     * @return ValidationMessages resource object to store validation messages about invalid user input.
     */
    @RequestMapping("/update/{id}")
    public RestResult<ValidationMessages> update(@PathVariable("id") final Long id, @RequestBody final FinanceRowItem newCostItem) {
        RestResult<FinanceRowItem> updateResult = financeRowService.updateCost(id, newCostItem).toGetResponse();
        if(updateResult.isFailure()){
            return RestResult.restFailure(updateResult.getFailure());
        }else{
            FinanceRowItem costItem = updateResult.getSuccessObject();
            ValidationMessages validationMessages = validationUtil.validateCostItem(costItem);
            return RestResult.restSuccess(validationMessages);
        }
    }

    @RequestMapping("/delete/{costId}")
    public RestResult<Void> delete(@PathVariable("costId") final Long costId) {
        return financeRowService.deleteCost(costId).toDeleteResponse();
    }
}
