package org.innovateuk.ifs.project.invite.controller;


import org.innovateuk.ifs.project.invite.transactional.AccFinanceContactInviteService;
import org.innovateuk.ifs.project.invite.transactional.AccInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/project/{projectId}/acc-finance-contact-invite")
public class AccFinanceContactInviteController extends AbstractAccInviteController{

    @Autowired
    private AccFinanceContactInviteService accFinanceContactInviteService;

    @Override
    protected AccInviteService getInviteService() {
        return accFinanceContactInviteService;
    }
}
