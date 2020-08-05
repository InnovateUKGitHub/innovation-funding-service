package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.transactional.ExternalRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/{userId}")
public class ExternalRoleController {

    @Autowired
    private ExternalRoleService externalRoleService;

    @PutMapping("/add-external-role")
    public RestResult<Void> updateUserStatus(@PathVariable long userId, @RequestBody Role role) {
        return externalRoleService.addUserRole(userId, role).toPutResponse();
    }
}
