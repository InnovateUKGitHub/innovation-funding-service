package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.finance.validator.FinanceValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;

/**
 * This RestController exposes CRUD operations to manage {@link ProjectFinanceRow} related data.
 */
@RestController
@RequestMapping({"/cost/project", "project-finance-row"})
public class ProjectFinanceRowController {

    @Autowired
    private ProjectFinanceRowService projectFinanceRowService;

    @Autowired
    private FinanceValidationUtil validationUtil;

    @PostMapping({"/add-with-response/{financeId}", ""})
    public RestResult<FinanceRowItem> addWithResponse(@RequestBody final FinanceRowItem financeRowItem) {
        return projectFinanceRowService.create(financeRowItem).toPostCreateResponse();
    }

    @GetMapping("/{id}")
    public RestResult<FinanceRowItem> get(@PathVariable final long id) {
        return projectFinanceRowService.get(id).toGetResponse();
    }

    /**
     * Save the updated FinanceRowItem in project finances and if there are validation messages, return those (but still save)
     * @return ValidationMessages resource object to store validation messages about invalid user input.
     */
    @PutMapping({"/update/{id}", "/{id}"})
    public RestResult<ValidationMessages> update(@PathVariable final long id, @RequestBody final FinanceRowItem financeRowItem) {
        ValidationMessages validationMessages = validationUtil.validateProjectCostItem(financeRowItem);
        if(!validationMessages.hasErrors()){
            RestResult<FinanceRowItem> updateResult = projectFinanceRowService.update(id, financeRowItem).toGetResponse();
            if (updateResult.isFailure()) {
                return restFailure(updateResult.getFailure());
            }
        }
        return restSuccess(validationMessages);
    }

    @DeleteMapping({"/delete/{costId}", "/{id}"})
    public RestResult<Void> delete(@PathVariable final long id) {
        return projectFinanceRowService.delete(id).toDeleteResponse();
    }
}
