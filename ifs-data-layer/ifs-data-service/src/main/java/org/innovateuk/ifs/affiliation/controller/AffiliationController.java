package org.innovateuk.ifs.affiliation.controller;

import org.innovateuk.ifs.affiliation.transactional.AffiliationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.AffiliationListResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * This RestController exposes CRUD operations to both the
 * {org.innovateuk.ifs.user.service.AffiliationRestServiceImpl} and other REST-API users
 * to manage {@link org.innovateuk.ifs.user.domain.Affiliation} related data.
 */
@RestController
@RequestMapping("/affiliation")
public class AffiliationController {

    @Autowired
    private AffiliationService affiliationService;

    @GetMapping("/id/{userId}/getUserAffiliations")
    public RestResult<AffiliationListResource> getUserAffiliations(@PathVariable("userId") long userId) {
        return affiliationService.getUserAffiliations(userId).toGetResponse();
    }

    @PutMapping("/id/{userId}/updateUserAffiliations")
    public RestResult<Void> updateUserAffiliations(@PathVariable("userId") long userId,
                                                   @Valid @RequestBody AffiliationListResource affiliations) {
        return affiliationService.updateUserAffiliations(userId, affiliations).toPutResponse();
    }
}
