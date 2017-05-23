package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProjectFinanceController exposes Project finance data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class ProjectFinanceController {
    @Autowired
    private ProjectFinanceRowService financeRowService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @GetMapping("/{projectId}/project-finances")
    public RestResult<List<ProjectFinanceResource>> getProjectFinances(@PathVariable("projectId") final Long projectId) {
        return financeCheckService.getProjectFinances(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}/partner-organisation/{organisationId}/viability")
    public RestResult<ViabilityResource> getViability(@PathVariable("projectId") final Long projectId,
                                                      @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.getViability(projectOrganisationCompositeId).toGetResponse();
    }

    @PostMapping("/{projectId}/partner-organisation/{organisationId}/viability/{viability}/{viabilityRagStatus}")
    public RestResult<Void> saveViability(@PathVariable("projectId") final Long projectId,
                                          @PathVariable("organisationId") final Long organisationId,
                                          @PathVariable("viability") final Viability viability,
                                          @PathVariable("viabilityRagStatus") final ViabilityRagStatus viabilityRagStatus) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.saveViability(projectOrganisationCompositeId, viability, viabilityRagStatus).toPostResponse();
    }

    @GetMapping("/{projectId}/partner-organisation/{organisationId}/eligibility")
    public RestResult<EligibilityResource> getEligibility(@PathVariable("projectId") final Long projectId,
                                                          @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.getEligibility(projectOrganisationCompositeId).toGetResponse();
    }

    @PostMapping("/{projectId}/partner-organisation/{organisationId}/eligibility/{eligibility}/{eligibilityRagStatus}")
    public RestResult<Void> saveEligibility(@PathVariable("projectId") final Long projectId,
                                            @PathVariable("organisationId") final Long organisationId,
                                            @PathVariable("eligibility") final Eligibility eligibility,
                                            @PathVariable("eligibilityRagStatus") final EligibilityRagStatus eligibilityRagStatus) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.saveEligibility(projectOrganisationCompositeId, eligibility, eligibilityRagStatus).toPostResponse();
    }

    @PostMapping("/{projectId}/partner-organisation/{organisationId}/credit-report/{reportPresent}")
    public RestResult<Void> saveCreditReport(@PathVariable("projectId") Long projectId, @PathVariable("organisationId") Long organisationId, @PathVariable("reportPresent") Boolean reportPresent) {
        return financeCheckService.saveCreditReport(projectId, organisationId, reportPresent).toPostResponse();
    }

    @GetMapping("/{projectId}/partner-organisation/{organisationId}/credit-report")
    public RestResult<Boolean> getCreditReport(@PathVariable("projectId") Long projectId, @PathVariable("organisationId") Long organisationId) {
        return financeCheckService.getCreditReport(projectId, organisationId).toGetResponse();
    }

    @GetMapping("/{projectId}/organisation/{organisationId}/financeDetails")
    public RestResult<ProjectFinanceResource> financeDetails(@PathVariable("projectId") final Long projectId, @PathVariable("organisationId") final Long organisationId) {
        return financeRowService.financeChecksDetails(projectId, organisationId).toGetResponse();
    }
}
