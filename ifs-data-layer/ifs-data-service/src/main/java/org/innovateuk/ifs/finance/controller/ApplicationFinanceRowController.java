package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceRowService;
import org.innovateuk.ifs.finance.validator.FinanceValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * This RestController exposes CRUD operations to both the
 * {@link org.innovateuk.ifs.finance.transactional.ApplicationFinanceRowServiceImpl} and other REST-API users
 * to manage {@link FinanceRow} related data.
 */
@RestController
@RequestMapping({"/cost", "/application-finance-row"})
public class ApplicationFinanceRowController {

    @Autowired
    private ApplicationFinanceRowService applicationFinanceRowService;

    @Autowired
    private FinanceValidationUtil validationUtil;

    @GetMapping("/{id}")
    public RestResult<FinanceRowItem> get(@PathVariable final long id) {
        return applicationFinanceRowService.get(id).toGetResponse();
    }

    @PostMapping
    public RestResult<FinanceRowItem> create(@RequestBody final FinanceRowItem financeRowItem) {
        return applicationFinanceRowService.create(financeRowItem.getTargetId(), financeRowItem).toPostCreateResponse();
    }
    
    /**
     * Save the updated FinanceRowItem and if there are validation messages, return those (but still save)
     * @return ValidationMessages resource object to store validation messages about invalid user input.
     */
    @PutMapping("/{id}")
    public RestResult<ValidationMessages> update(@PathVariable final long id, @RequestBody final FinanceRowItem financeRowItem) {
        RestResult<FinanceRowItem> updateResult = applicationFinanceRowService.update(id, financeRowItem).toGetResponse();
        if(updateResult.isFailure()){
            return RestResult.restFailure(updateResult.getFailure());
        } else {
            FinanceRowItem updatedFinanceRowItem = updateResult.getSuccess();
            ValidationMessages validationMessages = validationUtil.validateCostItem(updatedFinanceRowItem);
            return RestResult.restSuccess(validationMessages);
        }
    }

    @DeleteMapping("/{id}")
    public RestResult<Void> delete(@PathVariable final long id) {
        return applicationFinanceRowService.delete(id).toDeleteResponse();
    }
}
