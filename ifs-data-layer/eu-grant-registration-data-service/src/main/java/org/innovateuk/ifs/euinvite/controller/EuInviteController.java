package org.innovateuk.ifs.euinvite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.euinvite.transactional.EuInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for sending invites to eu registrants
 */
@RestController
public class EuInviteController {

    @Autowired
    EuInviteService euInviteService;

    @PostMapping("/eu-grants/send-invites")
    public RestResult<Void> sendInvites(@RequestBody List<UUID> ids) {
        return euInviteService.sendInvites(ids).toPostResponse();
    }
}