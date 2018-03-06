package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.transactional.CostTotalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * Controller concerned with handling {@link org.innovateuk.ifs.finance.domain.CostTotal}s.
 *
 * Typically these will be received as {@link FinanceCostTotalResource}s
 */
@RestController
public class CostTotalController {

    private CostTotalService costTotalService;

    @Autowired
    public CostTotalController(CostTotalService costTotalService) {
        this.costTotalService = costTotalService;
    }

    @PostMapping("/cost-totals")
    public RestResult<Void> addCostTotal(@NotNull @RequestBody FinanceCostTotalResource financeCostTotalResource) {
        return costTotalService.saveCostTotal(financeCostTotalResource).toPostCreateResponse();
    }
}
