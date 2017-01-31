package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.validator.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    /**
     * INFUND-4834 Note that this is done as GET to support some javascript features from front end.
     * It follows same rules as applicaiton finances do and reuses same javascripts.
     *
     * @param projectFinanceId
     * @param questionId
     * @param newCostItem
     * @return
     */
    @PostMapping("/add/{projectFinanceId}/{questionId}")
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

    /**
     * This is a way of adding empty rows to interface without actually persisting the empty row in database.
     * This is the way finances work so we are following same approach in PS.  It maybe possible to construct
     * required resource object without talking to data layer but that needs further investigation.
     * @param projectFinanceId
     * @param questionId
     * @return
     */
    @PostMapping("/add-without-persisting/{projectFinanceId}/{questionId}")
    public RestResult<FinanceRowItem> addProjectCostWithoutPersisting(
            @PathVariable("projectFinanceId") final Long projectFinanceId,
            @PathVariable("questionId") final Long questionId) {
        return projectFinanceRowService.addCostWithoutPersisting(projectFinanceId, questionId).toPostCreateResponse();
    }

    @GetMapping("/{id}")
    public RestResult<FinanceRowItem> get(@PathVariable("id") final Long id) {
        return projectFinanceRowService.getCostItem(id).toGetResponse();
    }

    /**
     * Save the updated FinanceRowItem and if there are validation messages, return those (but still save)
     * @return ValidationMessages resource object to store validation messages about invalid user input.
     */
    @PutMapping("/update/{id}")
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

    @DeleteMapping("/delete/{costId}")
    public RestResult<Void> delete(@PathVariable("costId") final Long costId) {
        return projectFinanceRowService.deleteCost(costId).toDeleteResponse();
    }
}
