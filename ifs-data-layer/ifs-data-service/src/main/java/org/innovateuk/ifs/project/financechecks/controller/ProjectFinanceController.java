package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
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
    private FinanceCheckService financeCheckService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

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
                                          @PathVariable("viability") final ViabilityState viability,
                                          @PathVariable("viabilityRagStatus") final ViabilityRagStatus viabilityRagStatus) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.saveViability(projectOrganisationCompositeId, viability, viabilityRagStatus).toPostResponse();
    }


    @PostMapping("/{projectId}/viability/reset")
    public RestResult<Void> resetViability(@PathVariable("projectId") final Long projectId) {
        return financeCheckService.resetViability(projectId).toPostResponse();
    }

    @PostMapping("/{projectId}/eligibility/reset")
    public RestResult<Void> resetEligibility(@PathVariable("projectId") final Long projectId) {
        return financeCheckService.resetEligibility(projectId).toPostResponse();
    }

    @PostMapping("/{projectId}/finance-checks/reset")
    public RestResult<Void> resetFinanceChecks(@PathVariable("projectId") final Long projectId) {
        return financeCheckService.resetFinanceChecks(projectId).toPostResponse();
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
                                            @PathVariable("eligibility") final EligibilityState eligibility,
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

    @GetMapping("/{projectId}/organisation/{organisationId}/finance-details")
    public RestResult<ProjectFinanceResource> financeDetails(@PathVariable("projectId") final Long projectId, @PathVariable("organisationId") final Long organisationId) {
        return projectFinanceService.financeChecksDetails(projectId, organisationId).toGetResponse();
    }

    @GetMapping("/{projectId}/finance/has-organisation-size-changed")
    public RestResult<Boolean> hasAnyProjectOrganisationSizeChangedFromApplication(@PathVariable long projectId) {
        return projectFinanceService.hasAnyProjectOrganisationSizeChangedFromApplication(projectId).toGetResponse();
    }

    @PostMapping("/{projectId}/partner-organisation/{organisationId}/milestones/approve")
    public RestResult<Void> approvePaymentMilestoneState(@PathVariable("projectId") final Long projectId,
                                                         @PathVariable("organisationId") final Long organisationId) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.approvePaymentMilestoneState(projectOrganisationCompositeId).toPostResponse();
    }

    @PostMapping("/{projectId}/partner-organisation/{organisationId}/milestones/reset")
    public RestResult<Void> resetPaymentMilestoneState(@PathVariable("projectId") final Long projectId,
                                                         @PathVariable("organisationId") final Long organisationId) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.resetPaymentMilestoneState(projectOrganisationCompositeId).toPostResponse();
    }

    @GetMapping("/{projectId}/partner-organisation/{organisationId}/milestones/state")
    public RestResult<ProjectProcurementMilestoneResource> getPaymentMilestoneState(@PathVariable("projectId") final Long projectId,
                                                                                    @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.getPaymentMilestone(projectOrganisationCompositeId).toGetResponse();
    }
}