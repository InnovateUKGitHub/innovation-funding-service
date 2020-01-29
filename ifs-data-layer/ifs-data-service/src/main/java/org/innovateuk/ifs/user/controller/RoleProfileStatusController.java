package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.transactional.RoleProfileStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/{userId}")
public class RoleProfileStatusController {

    @Autowired
    private RoleProfileStatusService roleProfileStatusService;

    @GetMapping("/role-profile-status/{profileRole}")
    public RestResult<RoleProfileStatusResource> findByUserIdAndProfileRole(@PathVariable long userId, @PathVariable final ProfileRole profileRole) {
        return roleProfileStatusService.findByUserIdAndProfileRole(userId, profileRole).toGetResponse();
    }

    @GetMapping("/role-profile-status")
    public RestResult<List<RoleProfileStatusResource>> findByUserId(@PathVariable long userId) {
        return roleProfileStatusService.findByUserId(userId).toGetResponse();
    }

    @PutMapping("/role-profile-status")
    public RestResult<Void> updateUserStatus(@PathVariable long userId, @RequestBody RoleProfileStatusResource roleProfileStatusResource) {
        return roleProfileStatusService.updateUserStatus(userId, roleProfileStatusResource).toPutResponse();
    }
}