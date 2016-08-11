package com.worth.ifs.invite.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.invite.transactional.InviteProjectService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;
import javax.validation.Valid;

/**
 * Project Invite controller to handle RESTful service related to Project Invite
 */

@RestController
@RequestMapping("/projectinvite")
public class InviteProjectController {

    private static final Log LOG = LogFactory.getLog(InviteController.class);

    @Autowired
    private InviteProjectService inviteProjectService;


    @RequestMapping(value = "/save-finance-contact-invite", method = RequestMethod.PUT)
    public RestResult<Void> saveProjectInvites(@RequestBody @Valid InviteProjectResource inviteProjectResource) {
        return inviteProjectService.saveFinanceContactInvite(inviteProjectResource).
                toPutResponse();
    }

    @RequestMapping(value = "/getProjectInviteByHash/{hash}", method = RequestMethod.GET)
    public RestResult<InviteProjectResource> getProjectInviteByHash(@PathVariable("hash") String hash) {
        return inviteProjectService.getInviteByHash(hash).toGetResponse();
    }

    @RequestMapping(value = "/getInvitesByProjectId/{projectId}", method = RequestMethod.GET)
    public RestResult<List<InviteProjectResource>> getInvitesByProject(@PathVariable("projectId") Long projectId) {
        return inviteProjectService.getInvitesByProject(projectId).toGetResponse();
    }

    @RequestMapping(value = "/acceptInvite/{hash}/{userId}", method = RequestMethod.POST)
    public RestResult<Void> acceptInvite( @PathVariable("hash") String hash, @PathVariable("userId") Long userId) {
        return inviteProjectService.acceptProjectInvite(hash, userId).toPostResponse();
    }

    @RequestMapping(value = "/checkExistingUser/{hash}", method = RequestMethod.GET)
    public RestResult<Void> checkExistingUser( @PathVariable("hash") String hash) {
        return inviteProjectService.checkUserExistingByInviteHash(hash).toGetResponse();
    }

}