package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.transactional.InviteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

