package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.crm.transactional.CrmService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.invite.transactional.AcceptApplicationInviteService;
import org.innovateuk.ifs.invite.transactional.ApplicationInviteService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.invite.resource.ApplicationInviteConstants.GET_USER_BY_HASH_MAPPING;

/**
 * InviteController is to handle the REST calls from the web-service and contains the handling of all call involving the Invite and InviteOrganisations.
 */

@RestController
@RequestMapping("/invite")
public class ApplicationInviteController {

    @Autowired
    private ApplicationInviteService applicationInviteService;

    @Autowired
    private AcceptApplicationInviteService acceptApplicationInviteService;

    @Autowired
    private CrmService crmService;

    @PostMapping("/createApplicationInvites")
    public RestResult<InviteResultsResource> createApplicationInvites(@RequestBody InviteOrganisationResource inviteOrganisationResource) {
        return applicationInviteService.createApplicationInvites(inviteOrganisationResource, Optional.empty()).toPostCreateResponse();
    }

    @PostMapping("/createApplicationInvites/{applicationId}")
    public RestResult<InviteResultsResource> createApplicationInvitesForApplication(@RequestBody InviteOrganisationResource inviteOrganisationResource, @PathVariable("applicationId") long applicationId) {
        return applicationInviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId)).toPostCreateResponse();
    }

    @GetMapping("/getInviteByHash/{hash}")
    public RestResult<ApplicationInviteResource> getInviteByHash(@PathVariable("hash") String hash) {
        return applicationInviteService.getInviteByHash(hash).toGetResponse();
    }

    @GetMapping("/getInviteOrganisationByHash/{hash}")
    public RestResult<InviteOrganisationResource> getInviteOrganisationByHash(@PathVariable("hash") String hash) {
        return applicationInviteService.getInviteOrganisationByHash(hash).toGetResponse();
    }

    @GetMapping("/getInvitesByApplicationId/{applicationId}")
    public RestResult<List<InviteOrganisationResource>> getInvitesByApplication(@PathVariable("applicationId") Long applicationId) {
        return applicationInviteService.getInvitesByApplication(applicationId).toGetResponse();
    }

    @PostMapping("/saveInvites")
    public RestResult<InviteResultsResource> saveInvites(@RequestBody List<ApplicationInviteResource> inviteResources) {
        return applicationInviteService.saveInvites(inviteResources).toPostCreateResponse();
    }

    @PutMapping("/acceptInvite/{hash}/{userId}")
    public RestResult<Void> acceptInvite( @PathVariable("hash") String hash, @PathVariable("userId") Long userId) {
        return acceptApplicationInviteService.acceptInvite(hash, userId)
                .andOnSuccessReturn(result -> {
                    crmService.syncCrmContact(userId);
                    return result;
                })
                .toPutResponse();
    }

    @DeleteMapping("/removeInvite/{inviteId}")
    public RestResult<Void> removeApplicationInvite(@PathVariable("inviteId") long applicationInviteResourceId) {
        return applicationInviteService.removeApplicationInvite(applicationInviteResourceId).toDeleteResponse();
    }

    @GetMapping("/checkExistingUser/{inviteHash}")
    public RestResult<Boolean> checkExistingUser(@PathVariable("inviteHash") String inviteHash) {
        return applicationInviteService.checkUserExistsForInvite(inviteHash).toGetResponse();
    }

    @GetMapping(GET_USER_BY_HASH_MAPPING + "{inviteHash}")
    public RestResult<UserResource> getUser(@PathVariable("inviteHash") String inviteHash) {
        return applicationInviteService.getUserByInviteHash(inviteHash).toGetResponse();
    }
}
