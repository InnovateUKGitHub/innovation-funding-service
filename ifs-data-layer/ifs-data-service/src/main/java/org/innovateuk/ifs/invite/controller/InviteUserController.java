package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.invite.transactional.InviteUserService;
import org.innovateuk.ifs.user.resource.SearchCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Invite User controller to handle RESTful services related to invites for users
 */

@RestController
@RequestMapping("/inviteUser")
public class InviteUserController {

    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String DEFAULT_PAGE_SIZE = "40";

    private static final Sort DEFAULT_INVITE_SORT = new Sort(new Sort.Order(Sort.Direction.ASC, "name"));

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

    @GetMapping("/checkExistingUser/{inviteHash}")
    public RestResult<Boolean> checkExistingUser(@PathVariable("inviteHash") String inviteHash) {
        return inviteUserService.checkExistingUser(inviteHash).toGetResponse();
    }

    @GetMapping("/internal/pending")
    public RestResult<RoleInvitePageResource> findPendingInternalUserInvites(@RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                       @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize){
        return inviteUserService.findPendingInternalUserInvites(new PageRequest(pageIndex, pageSize, DEFAULT_INVITE_SORT)).toGetResponse();
    }

    @PutMapping("/internal/pending/{inviteId}/resend")
    public RestResult<Void> resendPendingInternalUserInvite(@PathVariable("inviteId") long inviteId) {
        return inviteUserService.resendInternalUserInvite(inviteId).toPutResponse();
    }

    @GetMapping("/findExternalInvites")
    public RestResult<List<ExternalInviteResource>> findExternalInvites(@RequestParam(value = "searchString") final String searchString,
                                                                        @RequestParam(value = "searchCategory") final SearchCategory searchCategory) {
        return inviteUserService.findExternalInvites(searchString, searchCategory).toGetResponse();
    }
}

