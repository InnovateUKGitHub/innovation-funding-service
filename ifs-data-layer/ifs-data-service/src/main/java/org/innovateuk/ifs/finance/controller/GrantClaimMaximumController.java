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

    @GetMapping("/get-for-competition-type/{competitionTypeId}")
    public RestResult<Set<Long>> getGrantClaimMaximumsForCompetitionType(@PathVariable("competitionTypeId") final long competitionTypeId) {
        return grantClaimMaximumService.getGrantClaimMaximumsForCompetitionType(competitionTypeId).toGetResponse();
    }

    @PostMapping("/")
    public RestResult<GrantClaimMaximumResource> update(@RequestBody final GrantClaimMaximumResource gcm) {
        return grantClaimMaximumService.save(gcm).toPostCreateResponse();
    }
}
