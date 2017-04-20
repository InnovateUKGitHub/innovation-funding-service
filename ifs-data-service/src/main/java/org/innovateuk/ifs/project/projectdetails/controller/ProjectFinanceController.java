package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.spendprofile.service.SpendProfileService;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.transactional.ProjectGrantOfferService;
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
    private SpendProfileService spendProfileService;

    @Autowired
    private ProjectGrantOfferService projectGrantOfferService;

    @PostMapping("/{projectId}/spend-profile/generate")
    public RestResult<Void> generateSpendProfile(@PathVariable("projectId") final Long projectId) {
        return spendProfileService.generateSpendProfile(projectId).toPostCreateResponse();
    }

    /**
     * This method was written to recreate Spend Profile for one of the partner organisations on Production.
     *
     * This method assumes that all the necessary stuff is in the database before the Spend Profile can be generated.
     * This does not perform any validations to check that the Finance Checks are complete, Viability is approved,
     * Eligibility is approved or if the Spend Profile is already generated.
     */
    @PostMapping("/{projectId}/partner-organisation/{organisationId}/user/{userId}/spend-profile/generate")
    public RestResult<Void> generateSpendProfileForPartnerOrganisation(@PathVariable("projectId") final Long projectId,
                                                                       @PathVariable("organisationId") final Long organisationId,
                                                                       @PathVariable("userId") final Long userId) {
        return spendProfileService.generateSpendProfileForPartnerOrganisation(projectId, organisationId, userId).toPostCreateResponse();
    }

    @PostMapping("/{projectId}/spend-profile/approval/{approvalType}")
    public RestResult<Void> approveOrRejectSpendProfile(@PathVariable("projectId") final Long projectId,
                                                        @PathVariable("approvalType") final ApprovalType approvalType) {
        return spendProfileService.approveOrRejectSpendProfile(projectId, approvalType).toPostResponse();
    }

    @GetMapping("/{projectId}/spend-profile/approval")
    public RestResult<ApprovalType> getSpendProfileStatusByProjectId(@PathVariable("projectId") final Long projectId) {
        return spendProfileService.getSpendProfileStatusByProjectId(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}/partner-organisation/{organisationId}/spend-profile-table")
    public RestResult<SpendProfileTableResource> getSpendProfileTable(@PathVariable("projectId") final Long projectId,
                                                                      @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        return spendProfileService.getSpendProfileTable(projectOrganisationCompositeId).toGetResponse();
    }

    @GetMapping("/{projectId}/partner-organisation/{organisationId}/spend-profile-csv")
    public RestResult<SpendProfileCSVResource> getSpendProfileCSV(@PathVariable("projectId") final Long projectId,
                                                                  @PathVariable("organisationId") final Long organisationId) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return spendProfileService.getSpendProfileCSV(projectOrganisationCompositeId).toGetResponse();
    }

    @GetMapping("/{projectId}/partner-organisation/{organisationId}/spend-profile")
    public RestResult<SpendProfileResource> getSpendProfile(@PathVariable("projectId") final Long projectId,
                                                            @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return spendProfileService.getSpendProfile(projectOrganisationCompositeId).toGetResponse();
    }

    @PostMapping("/{projectId}/partner-organisation/{organisationId}/spend-profile")
    public RestResult<Void> saveSpendProfile(@PathVariable("projectId") final Long projectId,
                                             @PathVariable("organisationId") final Long organisationId,
                                             @RequestBody SpendProfileTableResource table) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return spendProfileService.saveSpendProfile(projectOrganisationCompositeId, table).toPostResponse();
    }

    @PostMapping("/{projectId}/partner-organisation/{organisationId}/spend-profile/complete")
    public RestResult<Void> markSpendProfileComplete(@PathVariable("projectId") final Long projectId,
                                                    @PathVariable("organisationId") final Long organisationId) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return spendProfileService.markSpendProfileComplete(projectOrganisationCompositeId).toPostResponse();
    }

    @PostMapping("/{projectId}/partner-organisation/{organisationId}/spend-profile/incomplete")
    public RestResult<Void> markSpendProfileIncomplete(@PathVariable("projectId") final Long projectId,
                                                    @PathVariable("organisationId") final Long organisationId) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return spendProfileService.markSpendProfileIncomplete(projectOrganisationCompositeId).toPostResponse();
    }

    @PostMapping("/{projectId}/complete-spend-profiles-review")
    public RestResult<Void> completeSpendProfilesReview(@PathVariable("projectId") final Long projectId) {
        return spendProfileService.completeSpendProfilesReview(projectId).toPostResponse();
    }

    @GetMapping("/{projectId}/project-finances")
    public RestResult<List<ProjectFinanceResource>> getProjectFinances(@PathVariable("projectId") final Long projectId) {
        return spendProfileService.getProjectFinances(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}/partner-organisation/{organisationId}/viability")
    public RestResult<ViabilityResource> getViability(@PathVariable("projectId") final Long projectId,
                                                      @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return spendProfileService.getViability(projectOrganisationCompositeId).toGetResponse();
    }

    @PostMapping("/{projectId}/partner-organisation/{organisationId}/viability/{viability}/{viabilityRagStatus}")
    public RestResult<Void> saveViability(@PathVariable("projectId") final Long projectId,
                                          @PathVariable("organisationId") final Long organisationId,
                                          @PathVariable("viability") final Viability viability,
                                          @PathVariable("viabilityRagStatus") final ViabilityRagStatus viabilityRagStatus) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return spendProfileService.saveViability(projectOrganisationCompositeId, viability, viabilityRagStatus).toPostResponse();
    }

    @GetMapping("/{projectId}/partner-organisation/{organisationId}/eligibility")
    public RestResult<EligibilityResource> getEligibility(@PathVariable("projectId") final Long projectId,
                                                          @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return spendProfileService.getEligibility(projectOrganisationCompositeId).toGetResponse();
    }

    @PostMapping("/{projectId}/partner-organisation/{organisationId}/eligibility/{eligibility}/{eligibilityRagStatus}")
    public RestResult<Void> saveEligibility(@PathVariable("projectId") final Long projectId,
                                            @PathVariable("organisationId") final Long organisationId,
                                            @PathVariable("eligibility") final Eligibility eligibility,
                                            @PathVariable("eligibilityRagStatus") final EligibilityRagStatus eligibilityRagStatus) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return spendProfileService.saveEligibility(projectOrganisationCompositeId, eligibility, eligibilityRagStatus).toPostResponse();
    }

    @PostMapping("/{projectId}/partner-organisation/{organisationId}/credit-report/{reportPresent}")
    public RestResult<Void> saveCreditReport(@PathVariable("projectId") Long projectId, @PathVariable("organisationId") Long organisationId, @PathVariable("reportPresent") Boolean reportPresent) {
        return spendProfileService.saveCreditReport(projectId, organisationId, reportPresent).toPostResponse();
    }

    @GetMapping("/{projectId}/partner-organisation/{organisationId}/credit-report")
    public RestResult<Boolean> getCreditReport(@PathVariable("projectId") Long projectId, @PathVariable("organisationId") Long organisationId) {
        return spendProfileService.getCreditReport(projectId, organisationId).toGetResponse();
    }

    @GetMapping("/{projectId}/organisation/{organisationId}/financeDetails")
    public RestResult<ProjectFinanceResource> financeDetails(@PathVariable("projectId") final Long projectId, @PathVariable("organisationId") final Long organisationId) {
        return financeRowService.financeChecksDetails(projectId, organisationId).toGetResponse();
    }
}
