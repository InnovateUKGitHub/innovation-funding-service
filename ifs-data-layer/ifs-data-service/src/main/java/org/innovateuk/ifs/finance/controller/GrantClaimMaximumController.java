package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This RestController exposes CRUD operations to both the
 * REST service and other REST-API users
 * to manage {@link org.innovateuk.ifs.finance.domain.GrantClaimMaximum} related data.
 */
@RestController
@RequestMapping("/grantClaimMaximum")
public class GrantClaimMaximumController {

    private GrantClaimMaximumService grantClaimMaximumService;

    public GrantClaimMaximumController(GrantClaimMaximumService grantClaimMaximumService) {
        this.grantClaimMaximumService = grantClaimMaximumService;
    }

    @GetMapping("/{id}")
    public RestResult<GrantClaimMaximumResource> getCompetitionById(@PathVariable("id") final long id) {
        return grantClaimMaximumService.getGrantClaimMaximumById(id).toGetResponse();
    }

}
