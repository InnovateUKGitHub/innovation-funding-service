package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.validator.util.ValidationUtil;

/**
 * This RestController exposes CRUD operations to both the
 * {@link org.innovateuk.ifs.finance.service.FinanceRowRestServiceImpl} and other REST-API users
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

    @PostMapping("/add/{applicationFinanceId}/{questionId}")
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
    
    @PostMapping("/add-without-persisting/{applicationFinanceId}/{questionId}")
    public RestResult<FinanceRowItem> addWithoutPersisting(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId,
            @PathVariable("questionId") final Long questionId) {
        return financeRowService.addCostWithoutPersisting(applicationFinanceId, questionId).toPostCreateResponse();
    }

    @GetMapping("/{id}")
    public RestResult<FinanceRowItem> get(@PathVariable("id") final Long id) {
        return financeRowService.getCostItem(id).toGetResponse();
    }

    /**
     * Save the updated FinanceRowItem and if there are validation messages, return those (but still save)
     * @return ValidationMessages resource object to store validation messages about invalid user input.
     */
    @PutMapping("/update/{id}")
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

    @DeleteMapping("/delete/{costId}")
    public RestResult<Void> delete(@PathVariable("costId") final Long costId) {
        return financeRowService.deleteCost(costId).toDeleteResponse();
    }
}
