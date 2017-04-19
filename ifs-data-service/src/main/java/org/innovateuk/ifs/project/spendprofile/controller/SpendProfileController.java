package org.innovateuk.ifs.project.spendprofile.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.financecheck.transactional.SpendProfileService;
import org.innovateuk.ifs.project.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project/{projectId}")
public class SpendProfileController {
    @Autowired
    private SpendProfileService spendProfileService;

    @PostMapping("/spend-profile/generate")
    public RestResult<Void> generateSpendProfile(@PathVariable("projectId") final Long projectId) {
        return spendProfileService.generateSpendProfile(projectId).toPostCreateResponse();
    }

    /**
     * This method assumes that all the necessary stuff is in the database before the Spend Profile can be generated.
     * This does not perform any validations to check that the Finance Checks are complete, Viability is approved,
     * Eligibility is approved or if the Spend Profile is already generated.
     */
    @PostMapping("/partner-organisation/{organisationId}/user/{userId}/spend-profile/generate")
    public RestResult<Void> generateSpendProfileForPartnerOrganisation(@PathVariable("projectId") final Long projectId,
                                                                       @PathVariable("organisationId") final Long organisationId,
                                                                       @PathVariable("userId") final Long userId) {
        return spendProfileService.generateSpendProfileForPartnerOrganisation(projectId, organisationId, userId).toPostCreateResponse();
    }

    @PostMapping("/spend-profile/approval/{approvalType}")
    public RestResult<Void> approveOrRejectSpendProfile(@PathVariable("projectId") final Long projectId,
                                                        @PathVariable("approvalType") final ApprovalType approvalType) {
        return spendProfileService.approveOrRejectSpendProfile(projectId, approvalType).toPostResponse();
    }

    @GetMapping("/spend-profile/approval")
    public RestResult<ApprovalType> getSpendProfileStatusByProjectId(@PathVariable("projectId") final Long projectId) {
        return spendProfileService.getSpendProfileStatusByProjectId(projectId).toGetResponse();
    }

    @GetMapping("/partner-organisation/{organisationId}/spend-profile-table")
    public RestResult<SpendProfileTableResource> getSpendProfileTable(@PathVariable("projectId") final Long projectId,
                                                                      @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        return spendProfileService.getSpendProfileTable(projectOrganisationCompositeId).toGetResponse();
    }

    @GetMapping("/partner-organisation/{organisationId}/spend-profile-csv")
    public RestResult<SpendProfileCSVResource> getSpendProfileCSV(@PathVariable("projectId") final Long projectId,
                                                                  @PathVariable("organisationId") final Long organisationId) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return spendProfileService.getSpendProfileCSV(projectOrganisationCompositeId).toGetResponse();
    }

    @GetMapping("/partner-organisation/{organisationId}/spend-profile")
    public RestResult<SpendProfileResource> getSpendProfile(@PathVariable("projectId") final Long projectId,
                                                            @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return spendProfileService.getSpendProfile(projectOrganisationCompositeId).toGetResponse();
    }

    @PostMapping("/partner-organisation/{organisationId}/spend-profile")
    public RestResult<Void> saveSpendProfile(@PathVariable("projectId") final Long projectId,
                                             @PathVariable("organisationId") final Long organisationId,
                                             @RequestBody SpendProfileTableResource table) {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return spendProfileService.saveSpendProfile(projectOrganisationCompositeId, table).toPostResponse();
    }

    @PostMapping("/partner-organisation/{organisationId}/spend-profile/complete")
    public RestResult<Void> markSpendProfileComplete(@PathVariable("projectId") final Long projectId,
                                                     @PathVariable("organisationId") final Long organisationId) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return spendProfileService.markSpendProfileComplete(projectOrganisationCompositeId).toPostResponse();
    }

    @PostMapping("/partner-organisation/{organisationId}/spend-profile/incomplete")
    public RestResult<Void> markSpendProfileIncomplete(@PathVariable("projectId") final Long projectId,
                                                       @PathVariable("organisationId") final Long organisationId) {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        return spendProfileService.markSpendProfileIncomplete(projectOrganisationCompositeId).toPostResponse();
    }

    @PostMapping("/complete-spend-profiles-review")
    public RestResult<Void> completeSpendProfilesReview(@PathVariable("projectId") final Long projectId) {
        return spendProfileService.completeSpendProfilesReview(projectId).toPostResponse();
    }
}
