package org.innovateuk.ifs.grants.controller;

import org.innovateuk.ifs.grants.transactional.GrantsInviteService;
import org.innovateuk.ifs.grants.transactional.GrantsProjectManagerInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project/{projectId}/acc-project-manger-invite")
public class GrantsProjectManagerInviteController extends AbstractGrantsInviteController {

    @Autowired
    private GrantsProjectManagerInviteService grantsProjectManagerInviteService;

    @Override
    protected GrantsInviteService getInviteService() {
        return grantsProjectManagerInviteService;
    }
}