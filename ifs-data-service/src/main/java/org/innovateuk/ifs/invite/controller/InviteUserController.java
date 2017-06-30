package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.invite.transactional.InviteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Invite User controller to handle RESTful services related to invites for users
 */

@RestController
@RequestMapping("/inviteUser")
public class InviteUserController {

    @Autowired
    private InviteUserService inviteUserService;

    @PostMapping("/saveInvite")
    public RestResult<Void> saveUserInvite(@RequestBody InviteUserResource inviteUserResource) {

        return inviteUserService.saveUserInvite(inviteUserResource.getInvitedUser(), inviteUserResource.getAdminRoleType()).toPostResponse();
    }

    @GetMapping("/getInvite/{inviteHash}")
    public RestResult<RoleInviteResource> getInvite(@PathVariable("inviteHash") String inviteHash){
        return inviteUserService.getInvite(inviteHash).toGetResponse();
    }
}

