package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.transactional.CostTotalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Controller concerned with handling {@link org.innovateuk.ifs.finance.domain.CostTotal}s.
 * <p>
 * Typically these will be received as {@link FinanceCostTotalResource}s
 */
@RestController
public class CostTotalController {

    private CostTotalService costTotalService;

    @Autowired
    public CostTotalController(CostTotalService costTotalService) {
        this.costTotalService = costTotalService;
    }

    @PostMapping("/cost-total")
    public RestResult<Void> addCostTotal(@NotNull @RequestBody FinanceCostTotalResource financeCostTotalResource) {
        return costTotalService.saveCostTotal(financeCostTotalResource).toPostCreateResponse();
    }

    @PostMapping("/cost-totals")
    public RestResult<Void> addCostTotals(@NotNull @RequestBody List<FinanceCostTotalResource> financeCostTotalResources) {
        return costTotalService.saveCostTotals(financeCostTotalResources).toPostCreateResponse();
    }
}
