package org.innovateuk.ifs.project.invite.controller;

import org.innovateuk.ifs.project.invite.transactional.AccFinanceContactInviteService;
import org.innovateuk.ifs.project.invite.transactional.AccInviteService;
import org.innovateuk.ifs.project.invite.transactional.AccProjectManagerInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project/{projectId}/acc-project-manger-invite")
public class AccProjectManagerInviteController extends AbstractAccInviteController {

    @Autowired
    private AccProjectManagerInviteService accProjectManagerInviteService;

    @Override
    protected AccInviteService getInviteService() {
        return accProjectManagerInviteService;
    }
}