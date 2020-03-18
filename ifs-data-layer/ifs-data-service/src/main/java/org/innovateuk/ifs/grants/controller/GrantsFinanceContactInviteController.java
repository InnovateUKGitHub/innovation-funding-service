package org.innovateuk.ifs.grants.controller;


import org.innovateuk.ifs.grants.transactional.GrantsFinanceContactInviteService;
import org.innovateuk.ifs.grants.transactional.GrantsInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/project/{projectId}/acc-finance-contact-invite")
public class GrantsFinanceContactInviteController extends AbstractGrantsInviteController {

    @Autowired
    private GrantsFinanceContactInviteService grantsFinanceContactInviteService;

    @Override
    protected GrantsInviteService getInviteService() {
        return grantsFinanceContactInviteService;
    }
}
