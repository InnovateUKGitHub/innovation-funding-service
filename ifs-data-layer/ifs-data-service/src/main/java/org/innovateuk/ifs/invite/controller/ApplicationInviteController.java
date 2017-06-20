package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.invite.transactional.InviteService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.innovateuk.ifs.invite.resource.ApplicationInviteConstants.GET_USER_BY_HASH_MAPPING;

/**
 * InviteController is to handle the REST calls from the web-service and contains the handling of all call involving the Invite and InviteOrganisations.
 */

@RestController
@RequestMapping("/invite")
public class ApplicationInviteController {
    private static final Log LOG = LogFactory.getLog(ApplicationInviteController.class);

    @Autowired
    private InviteService inviteService;

    @PostMapping("/createApplicationInvites")
    public RestResult<InviteResultsResource> createApplicationInvites(@RequestBody InviteOrganisationResource inviteOrganisationResource) {
        return inviteService.createApplicationInvites(inviteOrganisationResource).toPostCreateResponse();
    }

    @GetMapping("/getInviteByHash/{hash}")
    public RestResult<ApplicationInviteResource> getInviteByHash(@PathVariable("hash") String hash) {
        return inviteService.getInviteByHash(hash).toGetResponse();
    }

    @GetMapping("/getInviteOrganisationByHash/{hash}")
    public RestResult<InviteOrganisationResource> getInviteOrganisationByHash(@PathVariable("hash") String hash) {
        return inviteService.getInviteOrganisationByHash(hash).toGetResponse();
    }

    @GetMapping("/getInvitesByApplicationId/{applicationId}")
    public RestResult<List<InviteOrganisationResource>> getInvitesByApplication(@PathVariable("applicationId") Long applicationId) {
        return inviteService.getInvitesByApplication(applicationId).toGetResponse();
    }

    @PostMapping("/saveInvites")
    public RestResult<InviteResultsResource> saveInvites(@RequestBody List<ApplicationInviteResource> inviteResources) {
        return inviteService.saveInvites(inviteResources).toPostCreateResponse();
    }

    @PutMapping("/acceptInvite/{hash}/{userId}")
    public RestResult<Void> acceptInvite( @PathVariable("hash") String hash, @PathVariable("userId") Long userId) {
        return inviteService.acceptInvite(hash, userId).toPutResponse();
    }

    @DeleteMapping("/removeInvite/{inviteId}")
    public RestResult<Void> removeApplicationInvite(@PathVariable("inviteId") long applicationInviteResourceId) {
        return inviteService.removeApplicationInvite(applicationInviteResourceId).toDeleteResponse();
    }

    @GetMapping("/checkExistingUser/{hash}")
    public RestResult<Boolean> checkExistingUser( @PathVariable("hash") String hash) {
        return inviteService.checkUserExistingByInviteHash(hash).toGetResponse();
    }

    @GetMapping(GET_USER_BY_HASH_MAPPING + "{hash}")
    public RestResult<UserResource> getUser( @PathVariable("hash") String hash) {
        return inviteService.getUserByInviteHash(hash).toGetResponse();
    }
}
