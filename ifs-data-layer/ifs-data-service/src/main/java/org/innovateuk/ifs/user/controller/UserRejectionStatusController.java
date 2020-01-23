package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.UserRejection;
import org.innovateuk.ifs.user.transactional.UserRejectionStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/{userId}")
public class UserRejectionStatusController {

    @Autowired
    private UserRejectionStatusService userRejectionStatusService;

    @PostMapping("/update-status")
    public RestResult<Void> updateUserStatus(@PathVariable long userId, @RequestBody final UserRejection userRejection) {
        return userRejectionStatusService.updateUserStatus(userId, userRejection).toPostResponse();
    }
}
