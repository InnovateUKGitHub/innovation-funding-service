package com.worth.ifs.invite.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.invite.transactional.InviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * InviteController is to handle the REST calls from the web-service and contains the handling of all call involving the Invite and InviteOrganisations.
 */

@RestController
@RequestMapping("/invite")
public class InviteController {

    @Autowired
    private InviteService inviteService;

    @RequestMapping("/createApplicationInvites")
    public RestResult<InviteResultsResource> createApplicationInvites(@RequestBody InviteOrganisationResource inviteOrganisationResource) {
        return inviteService.createApplicationInvites(inviteOrganisationResource).toDefaultRestResultForPostCreate();
    }

    @RequestMapping("/getInviteByHash/{hash}")
    public RestResult<InviteResource> getInviteByHash(@PathVariable("hash") String hash) {
        return inviteService.getInviteByHash(hash).toDefaultRestResultForGet();
    }

    @RequestMapping("/getInviteOrganisationByHash/{hash}")
    public RestResult<InviteOrganisationResource> getInviteOrganisationByHash(@PathVariable("hash") String hash) {
        return inviteService.getInviteOrganisationByHash(hash).toDefaultRestResultForGet();
    }


    @RequestMapping("/getInvitesByApplicationId/{applicationId}")
    public RestResult<Set<InviteOrganisationResource>> getInvitesByApplication(@PathVariable("applicationId") Long applicationId) {
        return inviteService.getInvitesByApplication(applicationId).toDefaultRestResultForGet();
    }

    @RequestMapping(value = "/saveInvites", method = RequestMethod.POST)
    public RestResult<InviteResultsResource> saveInvites(@RequestBody List<InviteResource> inviteResources) {
        return inviteService.saveInvites(inviteResources).toDefaultRestResultForPostCreate();
    }
}
