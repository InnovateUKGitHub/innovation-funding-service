package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * This RestController exposes CRUD operations to both the
 * REST service and other REST-API users
 * to manage {@link org.innovateuk.ifs.finance.domain.GrantClaimMaximum} related data.
 */
@RestController
@RequestMapping("/grant-claim-maximum")
public class GrantClaimMaximumController {

    private GrantClaimMaximumService grantClaimMaximumService;

    public GrantClaimMaximumController(GrantClaimMaximumService grantClaimMaximumService) {
        this.grantClaimMaximumService = grantClaimMaximumService;
    }

    @GetMapping("/{id}")
    public RestResult<GrantClaimMaximumResource> getGrantClaimMaximumById(@PathVariable("id") final long id) {
        return grantClaimMaximumService.getGrantClaimMaximumById(id).toGetResponse();
    }

    @PostMapping("/revert-to-default/{competitionId}")
    public RestResult<Set<Long>> revertToDefault(@PathVariable("competitionId") final long competitionId) {
        return grantClaimMaximumService.revertToDefault(competitionId).toGetResponse();
    }

    @PostMapping("/")
    public RestResult<GrantClaimMaximumResource> update(@RequestBody final GrantClaimMaximumResource gcm) {
        return grantClaimMaximumService.save(gcm).toPostCreateResponse();
    }

    @GetMapping("/maximum-funding-level-overridden/{competitionId}")
    public RestResult<Boolean> isMaximumFundingLevelOverridden(@PathVariable("competitionId") final long competitionId) {
        return grantClaimMaximumService.isMaximumFundingLevelOverridden(competitionId).toGetResponse();
    }
}
