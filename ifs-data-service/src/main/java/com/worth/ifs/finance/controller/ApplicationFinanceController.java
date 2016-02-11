package com.worth.ifs.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.transactional.CostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.finance.service.ApplicationFinanceRestServiceImpl} and other REST-API users
 * to manage {@link ApplicationFinance} related data.
 */
@RestController
@RequestMapping("/applicationfinance")
public class ApplicationFinanceController {

    public static final String RESEARCH_PARTICIPATION_PERCENTAGE = "researchParticipationPercentage";

    @Autowired
    private CostService costService;

    @RequestMapping("/findByApplicationOrganisation/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> findByApplicationOrganisation(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {

        return newRestHandler().perform(() -> costService.findApplicationFinanceByApplicationIdAndOrganisation(applicationId, organisationId));
    }

    @RequestMapping("/findByApplication/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> findByApplication(
            @PathVariable("applicationId") final Long applicationId) {

        return newRestHandler().perform(() -> costService.findApplicationFinanceByApplication(applicationId));
    }

    // TODO DW - INFUND-1555 - remove ObjectNode usage
    @RequestMapping("/getResearchParticipationPercentage/{applicationId}")
    public RestResult<ObjectNode> getResearchParticipationPercentage(@PathVariable("applicationId") final Long applicationId) {

        return newRestHandler().perform(() ->
            costService.getResearchParticipationPercentage(applicationId).andOnSuccess(percentage -> {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();
                node.put(RESEARCH_PARTICIPATION_PERCENTAGE, percentage);
                return serviceSuccess(node);
            })
        );
    }

    @RequestMapping("/add/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> add(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("organisationId") final Long organisationId) {

        return newRestHandler().perform(() -> costService.addCost(applicationId, organisationId));
    }

    @RequestMapping("/getById/{applicationFinanceId}")
    public RestResult<ApplicationFinanceResource> findOne(@PathVariable("applicationFinanceId") final Long applicationFinanceId) {
        return newRestHandler().perform(() -> costService.getApplicationFinanceById(applicationFinanceId));
    }

    @RequestMapping("/update/{applicationFinanceId}")
    public RestResult<ApplicationFinanceResource> update(@PathVariable("applicationFinanceId") final Long applicationFinanceId, @RequestBody final ApplicationFinanceResource applicationFinance) {
        return newRestHandler().perform(() -> costService.updateCost(applicationFinanceId, applicationFinance));
    }

    @RequestMapping("/financeDetails/{applicationId}/{organisationId}")
    public RestResult<ApplicationFinanceResource> financeDetails(@PathVariable("applicationId") final Long applicationId, @PathVariable("organisationId") final Long organisationId) {
        return newRestHandler().perform(() -> costService.financeDetails(applicationId, organisationId));
    }

    @RequestMapping("/financeTotals/{applicationId}")
    public RestResult<List<ApplicationFinanceResource>> financeTotals(@PathVariable("applicationId") final Long applicationId) {
        return newRestHandler().perform(() -> costService.financeTotals(applicationId));
    }
}
