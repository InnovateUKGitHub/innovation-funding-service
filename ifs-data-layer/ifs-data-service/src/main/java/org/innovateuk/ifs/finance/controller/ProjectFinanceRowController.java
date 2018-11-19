package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.application.validation.ApplicationValidationUtil;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;

/**
 * This RestController exposes CRUD operations to manage {@link ProjectFinanceRow} related data.
 */
@RestController
@RequestMapping("/cost/project")
public class ProjectFinanceRowController {

    @Autowired
    private ProjectFinanceRowService projectFinanceRowService;

    @Autowired
    private ApplicationValidationUtil validationUtil;

    /**
     * Used for adding new cost items to project costs table
     * @param projectFinanceId
     * @param questionId
     * @param newCostItem
     * @return
     */
    @PostMapping("/add/{projectFinanceId}/{questionId}")
    public RestResult<ValidationMessages> add(
            @PathVariable("projectFinanceId") final long projectFinanceId,
            @PathVariable("questionId") final long questionId,
            @RequestBody(required=false) final FinanceRowItem newCostItem) {
        ValidationMessages validationMessages = validationUtil.validateProjectCostItem(newCostItem);

        if (validationMessages.hasErrors()) {
            return restSuccess(validationMessages, HttpStatus.OK);
        } else {
            RestResult<FinanceRowItem> createResult = projectFinanceRowService.addCost(projectFinanceId, questionId, newCostItem).toPostCreateResponse();
            if (createResult.isFailure()) {
                return restFailure(createResult.getFailure());
            } else {
                validationMessages.setObjectId(createResult.getSuccess().getId());
                return restSuccess(validationMessages, HttpStatus.CREATED);
            }
        }
    }

    @PostMapping("/add-with-response/{financeId}")
    public RestResult<FinanceRowItem> addWithResponse(
            @PathVariable final long financeId,
            @RequestBody final FinanceRowItem newCostItem) {
        return projectFinanceRowService.addCost(financeId, newCostItem).toPostCreateResponse();
    }

    /**
     * Used to add new empty rows on load of project finances page.  This is done to be consistent with how application
     * finances work.  It may be possible to build resource object without going to data layer.  That will require
     * further investigation and out of scope form INFUND-4834.
     * @param projectFinanceId
     * @param questionId
     * @return
     */
    @PostMapping("/add-without-persisting/{projectFinanceId}/{questionId}")
    public RestResult<FinanceRowItem> addProjectCostWithoutPersisting(
            @PathVariable("projectFinanceId") final long projectFinanceId,
            @PathVariable("questionId") final long questionId) {
        return projectFinanceRowService.addCostWithoutPersisting(projectFinanceId, questionId).toPostCreateResponse();
    }

    @GetMapping("/{id}")
    public RestResult<FinanceRowItem> get(@PathVariable("id") final long id) {
        return projectFinanceRowService.getCostItem(id).toGetResponse();
    }

    /**
     * Save the updated FinanceRowItem in project finances and if there are validation messages, return those (but still save)
     * @return ValidationMessages resource object to store validation messages about invalid user input.
     */
    @PutMapping("/update/{id}")
    public RestResult<ValidationMessages> update(@PathVariable("id") final long id, @RequestBody final FinanceRowItem newCostItem) {
        ValidationMessages validationMessages = validationUtil.validateProjectCostItem(newCostItem);
        if(!validationMessages.hasErrors()){
            RestResult<FinanceRowItem> updateResult = projectFinanceRowService.updateCost(id, newCostItem).toGetResponse();
            if (updateResult.isFailure()) {
                return restFailure(updateResult.getFailure());
            }
        }
        return restSuccess(validationMessages);
    }

    @ZeroDowntime(reference = "IFS-3486", description = "Remove old mapping")
    @DeleteMapping({"/delete/{costId}", "/{projectId}/organisation/{organisationId}/delete/{costId}"})
    public RestResult<Void> delete(@PathVariable("costId") final long costId) {
        return projectFinanceRowService.deleteCost(costId).toDeleteResponse();
    }
}
