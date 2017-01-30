package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.validator.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This RestController exposes CRUD operations to manage {@link ProjectFinanceRow} related data.
 */
@RestController
@RequestMapping("/cost/project")
public class ProjectFinanceRowController {

    @Autowired
    private ProjectFinanceRowService projectFinanceRowService;

    @Autowired
    private ValidationUtil validationUtil;

    @RequestMapping("/add/{projectFinanceId}/{questionId}")
    public RestResult<ValidationMessages> add(
            @PathVariable("projectFinanceId") final Long projectFinanceId,
            @PathVariable("questionId") final Long questionId,
            @RequestBody(required=false) final FinanceRowItem newCostItem) {
        RestResult<FinanceRowItem> createResult = projectFinanceRowService.addCost(projectFinanceId, questionId, newCostItem).toPostCreateResponse();
        if(createResult.isFailure()){
            return RestResult.restFailure(createResult.getFailure());
        }else{
            FinanceRowItem costItem = createResult.getSuccessObject();
            ValidationMessages validationMessages = validationUtil.validateProjectCostItem(costItem);
            return RestResult.restSuccess(validationMessages, HttpStatus.CREATED);
        }
    }

    @RequestMapping("/add-without-persisting/{projectFinanceId}/{questionId}")
    public RestResult<FinanceRowItem> addProjectCostWithoutPersisting(
            @PathVariable("projectFinanceId") final Long projectFinanceId,
            @PathVariable("questionId") final Long questionId) {
        return projectFinanceRowService.addCostWithoutPersisting(projectFinanceId, questionId).toPostCreateResponse();
    }

    @RequestMapping("/{id}")
    public RestResult<FinanceRowItem> get(@PathVariable("id") final Long id) {
        return projectFinanceRowService.getCostItem(id).toGetResponse();
    }

    /**
     * Save the updated FinanceRowItem and if there are validation messages, return those (but still save)
     * @return ValidationMessages resource object to store validation messages about invalid user input.
     */
    @RequestMapping("/update/{id}")
    public RestResult<ValidationMessages> update(@PathVariable("id") final Long id, @RequestBody final FinanceRowItem newCostItem) {
        RestResult<FinanceRowItem> updateResult = projectFinanceRowService.updateCost(id, newCostItem).toGetResponse();
        if(updateResult.isFailure()){
            return RestResult.restFailure(updateResult.getFailure());
        }else{
            FinanceRowItem costItem = updateResult.getSuccessObject();
            ValidationMessages validationMessages = validationUtil.validateProjectCostItem(costItem);
            return RestResult.restSuccess(validationMessages);
        }
    }

    @RequestMapping("/delete/{costId}")
    public RestResult<Void> delete(@PathVariable("costId") final Long costId) {
        return projectFinanceRowService.deleteCost(costId).toDeleteResponse();
    }
}
