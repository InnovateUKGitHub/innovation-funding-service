package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.transactional.CostTotalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller concerned with handling {@link org.innovateuk.ifs.finance.domain.CostTotal}s.
 * <p>
 * Typically these will be received as {@link FinanceCostTotalResource}s
 */
@RestController
public class CostTotalController {

    private CostTotalService costTotalService;

    private static final Logger LOG = LoggerFactory.getLogger(CostTotalController.class);

    @Autowired
    public CostTotalController(CostTotalService costTotalService) {
        this.costTotalService = costTotalService;
    }

    @PostMapping("/cost-total")
    public RestResult<Void> addCostTotal(@NotNull @RequestBody FinanceCostTotalResource financeCostTotalResource) {
        return costTotalService.saveCostTotal(financeCostTotalResource).toPostCreateResponse();
    }

    @PostMapping("/cost-totals")
    public RestResult<Void> addCostTotals(@NotNull @RequestBody List<FinanceCostTotalResource>
                                                  financeCostTotalResources) {
        LOG.debug("Initiating addCostTotals for financeIds: {}",
                financeCostTotalResources.stream().map(financeCostTotalResource ->
                        String.valueOf(financeCostTotalResource.getFinanceId()))
                        .collect(Collectors.joining(", ")));

        return costTotalService.saveCostTotals(financeCostTotalResources).toPostCreateResponse();
    }
}
