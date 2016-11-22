package com.worth.ifs.invite.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.invite.transactional.InviteService;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static com.worth.ifs.invite.resource.ApplicationInviteConstants.GET_USER_BY_HASH_MAPPING;

/**
 * InviteController is to handle the REST calls from the web-service and contains the handling of all call involving the Invite and InviteOrganisations.
 */

@RestController
@RequestMapping("/invite")
public class ApplicationInviteController {
    private static final Log LOG = LogFactory.getLog(ApplicationInviteController.class);

    @Autowired
    private InviteService inviteService;

    @RequestMapping("/createApplicationInvites")
    public RestResult<InviteResultsResource> createApplicationInvites(@RequestBody InviteOrganisationResource inviteOrganisationResource) {
        return inviteService.createApplicationInvites(inviteOrganisationResource).toPostCreateResponse();
    }

    @RequestMapping("/getInviteByHash/{hash}")
    public RestResult<ApplicationInviteResource> getInviteByHash(@PathVariable("hash") String hash) {
        return inviteService.getInviteByHash(hash).toGetResponse();
    }

    @RequestMapping("/getInviteOrganisationByHash/{hash}")
    public RestResult<InviteOrganisationResource> getInviteOrganisationByHash(@PathVariable("hash") String hash) {
        return inviteService.getInviteOrganisationByHash(hash).toGetResponse();
    }

    @RequestMapping("/getInvitesByApplicationId/{applicationId}")
    public RestResult<Set<InviteOrganisationResource>> getInvitesByApplication(@PathVariable("applicationId") Long applicationId) {
        return inviteService.getInvitesByApplication(applicationId).toGetResponse();
    }

    @RequestMapping(value = "/saveInvites", method = RequestMethod.POST)
    public RestResult<InviteResultsResource> saveInvites(@RequestBody List<ApplicationInviteResource> inviteResources) {
        return inviteService.saveInvites(inviteResources).toPostCreateResponse();
    }

    @RequestMapping(value = "/acceptInvite/{hash}/{userId}", method = RequestMethod.PUT)
    public RestResult<Void> acceptInvite( @PathVariable("hash") String hash, @PathVariable("userId") Long userId) {
        return inviteService.acceptInvite(hash, userId).toPutResponse();
    }

    @RequestMapping(value = "/removeInvite/{inviteId}", method = RequestMethod.DELETE)
    public RestResult<Void> removeApplicationInvite(@PathVariable("inviteId") Long applicationInviteResourceId) {
        return inviteService.removeApplicationInvite(applicationInviteResourceId).toDeleteResponse();
    }

    @RequestMapping(value = "/checkExistingUser/{hash}", method = RequestMethod.GET)
    public RestResult<Boolean> checkExistingUser( @PathVariable("hash") String hash) {
        return inviteService.checkUserExistingByInviteHash(hash).toGetResponse();
    }

    @RequestMapping(value = GET_USER_BY_HASH_MAPPING + "{hash}", method = RequestMethod.GET)
    public RestResult<UserResource> getUser( @PathVariable("hash") String hash) {
        return inviteService.getUserByInviteHash(hash).toGetResponse();
    }

}
