package com.worth.ifs.finance.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.transactional.CostService;
import com.worth.ifs.validator.util.ValidationUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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

    @RequestMapping("/add/{applicationFinanceId}/{questionId}")
    public RestResult<CostItem> add(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId,
            @PathVariable("questionId") final Long questionId,
            @RequestBody(required=false) final CostItem newCostItem) {
        return costService.addCost(applicationFinanceId, questionId, newCostItem).toPostCreateResponse();
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
    public RestResult<ValidationMessages> update(@PathVariable("id") final Long id, @Valid @RequestBody final CostItem newCostItem, BindingResult bindingResult) {
        RestResult<Void> updateResult = costService.updateCost(id, newCostItem)
                .toPutResponse();
        LOG.info("CostController update: ");

        if(updateResult.isFailure()){
            return updateResult.toGetResponse(new ValidationMessages());
        }else{
            ValidationMessages validationMessages = ValidationUtil.validateCostItem(newCostItem);
            return RestResult.restSuccess(validationMessages);
        }
    }

    @RequestMapping("/delete/{costId}")
    public RestResult<Void> delete(@PathVariable("costId") final Long costId) {
        return costService.deleteCost(costId).toDeleteResponse();
    }
}
