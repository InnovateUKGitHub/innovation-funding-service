package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.application.validation.ApplicationValidationUtil;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.transactional.FinanceRowCostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * This RestController exposes CRUD operations to both the
 * {@link org.innovateuk.ifs.finance.service.FinanceRowRestServiceImpl} and other REST-API users
 * to manage {@link FinanceRow} related data.
 */
@RestController
@RequestMapping("/cost")
public class FinanceRowController {

    @Autowired
    private FinanceRowCostsService financeRowCostsService;

    @Autowired
    private ApplicationValidationUtil validationUtil;

    @PostMapping("/add/{applicationFinanceId}/{questionId}")
    public RestResult<ValidationMessages> add(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId,
            @PathVariable("questionId") final Long questionId,
            @RequestBody(required=false) final FinanceRowItem newCostItem) {
    	RestResult<FinanceRowItem> createResult = financeRowCostsService.addCost(applicationFinanceId, questionId, newCostItem).toPostCreateResponse();
        if(createResult.isFailure()){
            return RestResult.restFailure(createResult.getFailure());
        }else{
            FinanceRowItem costItem = createResult.getSuccess();
            ValidationMessages validationMessages = validationUtil.validateCostItem(costItem);
            return RestResult.restSuccess(validationMessages, HttpStatus.CREATED);
        }
    }
    
    @PostMapping("/add-without-persisting/{applicationFinanceId}/{questionId}")
    public RestResult<FinanceRowItem> addWithoutPersisting(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId,
            @PathVariable("questionId") final Long questionId) {
        return financeRowCostsService.addCostWithoutPersisting(applicationFinanceId, questionId).toPostCreateResponse();
    }

    @GetMapping("/{id}")
    public RestResult<FinanceRowItem> get(@PathVariable("id") final Long id) {
        return financeRowCostsService.getCostItem(id).toGetResponse();
    }

    /**
     * Save the updated FinanceRowItem and if there are validation messages, return those (but still save)
     * @return ValidationMessages resource object to store validation messages about invalid user input.
     */
    @PutMapping("/update/{id}")
    public RestResult<ValidationMessages> update(@PathVariable("id") final Long id, @RequestBody final FinanceRowItem newCostItem) {
        RestResult<FinanceRowItem> updateResult = financeRowCostsService.updateCost(id, newCostItem).toGetResponse();
        if(updateResult.isFailure()){
            return RestResult.restFailure(updateResult.getFailure());
        }else{
            FinanceRowItem costItem = updateResult.getSuccess();
            ValidationMessages validationMessages = validationUtil.validateCostItem(costItem);
            return RestResult.restSuccess(validationMessages);
        }
    }

    @DeleteMapping("/delete/{costId}")
    public RestResult<Void> delete(@PathVariable("costId") final Long costId) {
        return financeRowCostsService.deleteCost(costId).toDeleteResponse();
    }
}
