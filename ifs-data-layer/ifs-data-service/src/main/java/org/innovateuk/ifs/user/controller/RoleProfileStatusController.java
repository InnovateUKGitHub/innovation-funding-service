package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.transactional.RoleProfileStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/{userId}")
public class RoleProfileStatusController {

    @Autowired
    private RoleProfileStatusService roleProfileStatusService;

    @PostMapping("/update-status")
    public RestResult<Void> updateUserStatus(@PathVariable long userId, @RequestBody final RoleProfileStatusResource roleProfileStatusResource) {
        return roleProfileStatusService.updateUserStatus(userId, roleProfileStatusResource).toPostResponse();
    }
}
