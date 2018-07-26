package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * This RestController exposes CRUD operations to both the
 * REST service and other REST-API users
 * to manage {@link org.innovateuk.ifs.finance.domain.GrantClaimMaximum} related data.
 */
@RestController
@RequestMapping("/grantClaimMaximum")
public class GrantClaimMaximumController {

    @Autowired
    private GrantClaimMaximumService grantClaimMaximumService;

//    public GrantClaimMaximumController(GrantClaimMaximumService grantClaimMaximumService) {
//        this.grantClaimMaximumService = grantClaimMaximumService;
//    }

    @GetMapping("/{id}")
    public RestResult<GrantClaimMaximumResource> getGrantClaimMaximumById(@PathVariable("id") final long id) {
        return grantClaimMaximumService.getGrantClaimMaximumById(id).toGetResponse();
    }

    @PostMapping("/")
    public RestResult<GrantClaimMaximumResource> update(@RequestBody final GrantClaimMaximumResource gcm) {
        return grantClaimMaximumService.save(gcm).toPostCreateResponse();
    }
}
