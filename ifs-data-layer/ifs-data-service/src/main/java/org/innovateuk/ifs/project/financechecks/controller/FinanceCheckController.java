package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.domain.FinanceCheck;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.string.resource.StringResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * FinanceCheckController exposes {@link FinanceCheck} data and operations through a REST API.
 */
@RestController
@RequestMapping(FinanceCheckURIs.BASE_URL)
public class FinanceCheckController {

    @Autowired
    private FinanceCheckService financeCheckService;

    @GetMapping("/{projectId}" + FinanceCheckURIs.ORGANISATION_PATH + "/{organisationId}" + FinanceCheckURIs.PATH)
    public RestResult<FinanceCheckResource> getFinanceCheck(@PathVariable long projectId,
                                                            @PathVariable long organisationId) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.getByProjectAndOrganisation(projectOrganisationCompositeId).toGetResponse();
    }

    @GetMapping("/{projectId}" + FinanceCheckURIs.PATH)
    public RestResult<FinanceCheckSummaryResource> getFinanceCheckSummary(@PathVariable long projectId) {
        return financeCheckService.getFinanceCheckSummary(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}" + FinanceCheckURIs.PATH + "/overview")
    public RestResult<FinanceCheckOverviewResource> getFinanceCheckOverview(@PathVariable long projectId) {
        return financeCheckService.getFinanceCheckOverview(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}" + FinanceCheckURIs.ORGANISATION_PATH + "/{organisationId}" + FinanceCheckURIs.PATH + "/eligibility")
    public RestResult<FinanceCheckEligibilityResource> getFinanceCheckEligibilityDetails(@PathVariable long projectId,
                                                                                         @PathVariable long organisationId) {
        return financeCheckService.getFinanceCheckEligibilityDetails(projectId, organisationId).toGetResponse();
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
    public RestResult<Void> resetViability(@PathVariable("projectId") final Long projectId, @RequestBody(required = false) final StringResource reason) {
        String changeReason = reason == null ? null : reason.getContent();
        return financeCheckService.resetViability(projectId, changeReason).toPostResponse();
    }

    @PostMapping("/{projectId}/eligibility/reset")
    public RestResult<Void> resetEligibility(@PathVariable("projectId") final Long projectId, @RequestBody(required = false) final StringResource reason) {
        String changeReason = reason == null ? null : reason.getContent();
        return financeCheckService.resetEligibility(projectId, changeReason).toPostResponse();
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

    @PostMapping("/{projectId}/partner-organisation/{organisationId}/milestones/approve")
    public RestResult<Void> approvePaymentMilestoneState(@PathVariable("projectId") final Long projectId,
                                                         @PathVariable("organisationId") final Long organisationId) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.approvePaymentMilestoneState(projectOrganisationCompositeId).toPostResponse();
    }

    @PostMapping("/{projectId}/partner-organisation/{organisationId}/milestones/reset")
    public RestResult<Void> resetPaymentMilestoneState(@PathVariable("projectId") final Long projectId,
                                                       @PathVariable("organisationId") final Long organisationId,
                                                       @RequestBody(required = false) final StringResource reason) {
        String changeReason = reason == null ? null : reason.getContent();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.resetPaymentMilestoneState(projectOrganisationCompositeId, changeReason).toPostResponse();
    }

    @GetMapping("/{projectId}/partner-organisation/{organisationId}/milestones/state")
    public RestResult<PaymentMilestoneResource> getPaymentMilestoneState(@PathVariable("projectId") final Long projectId,
                                                                         @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.getPaymentMilestone(projectOrganisationCompositeId).toGetResponse();
    }
}