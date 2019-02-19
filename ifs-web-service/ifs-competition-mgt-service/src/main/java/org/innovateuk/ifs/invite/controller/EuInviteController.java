package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.invite.resource.EuContactPageResource;
import org.innovateuk.ifs.invite.resource.EuContactResource;
import org.innovateuk.ifs.invite.service.EuInviteService;
import org.innovateuk.ifs.invite.viewmodel.EuInviteViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@SecuredBySpring(value = "Controller",
        description = "Only comp admins and project finance have permission to see the list of eu registrants",
        securedType = EuInviteController.class)

@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
public class EuInviteController {

    private static final int DEFAULT_PAGE_SIZE = 100;

    @Autowired
    private EuInviteService euInviteService;

    @GetMapping("/invite-non-notified")
    public String viewNonNotifiedEuRegistrants(@RequestParam(value = "page", defaultValue = "0") int pageIndex,
                                               Model model) {
        EuContactPageResource euRegistrants = euInviteService.getEuContactsByNotified(false,
                                                                                      pageIndex,
                                                                                      DEFAULT_PAGE_SIZE).getSuccess();
        EuInviteViewModel viewModel = new EuInviteViewModel(euRegistrants.getContent(),
                                                            new Pagination(euRegistrants, ""));
        model.addAttribute("model", viewModel);
        return "non-notified";
    }

    @GetMapping("/invite-notified")
    public String viewNotifiedEuRegistrants(@RequestParam(value = "page", defaultValue = "0") int pageIndex,
                                            Model model) {
        EuContactPageResource euRegistrants = euInviteService.getEuContactsByNotified(true,
                                                                                      pageIndex,
                                                                                      DEFAULT_PAGE_SIZE).getSuccess();
        EuInviteViewModel viewModel = new EuInviteViewModel(euRegistrants.getContent(),
                                                            new Pagination(euRegistrants, ""));
        model.addAttribute("model", viewModel);
        return "notified";
    }
}
