package org.innovateuk.ifs.eu.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;

import org.innovateuk.ifs.eu.form.EuContactSelectionForm;
import org.innovateuk.ifs.eu.invite.EuInviteRestService;
import org.innovateuk.ifs.eugrant.EuContactPageResource;
import org.innovateuk.ifs.eu.viewmodel.EuInviteViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.google.common.primitives.Longs.asList;

/**
 * This controller will handle all requests related to inviting eu registrants onto the main IFS platform
 */

@Controller
@SecuredBySpring(value = "Controller",
        description = "Only comp admins and project finance have permission to see the list of eu registrants",
        securedType = EuInviteController.class)

@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
public class EuInviteController {

    private static final int DEFAULT_PAGE_SIZE = 100;

    @Autowired
    private EuInviteRestService euInviteRestService;

    @GetMapping("/eu-invite-non-notified")
    public String viewNonNotifiedEuRegistrants(@RequestParam(value = "page", defaultValue = "0") int pageIndex,
                                               EuContactSelectionForm form,
                                               Model model) {
        EuContactPageResource euRegistrants = euInviteRestService.getEuContactsByNotified(false,
                                                                                          pageIndex,
                                                                                          DEFAULT_PAGE_SIZE).getSuccess();
        EuInviteViewModel viewModel = new EuInviteViewModel(euRegistrants.getContent(),
                                                            new Pagination(euRegistrants, ""),
                                                            1200,
                                                            2100);
        model.addAttribute("model", viewModel);
        return "eu/non-notified";
    }

    @GetMapping("/eu-invite-notified")
    public String viewNotifiedEuRegistrants(@RequestParam(value = "page", defaultValue = "0") int pageIndex,
                                            EuContactSelectionForm form,
                                            Model model) {
        EuContactPageResource euRegistrants = euInviteRestService.getEuContactsByNotified(true,
                                                                                      pageIndex,
                                                                                      DEFAULT_PAGE_SIZE).getSuccess();
        EuInviteViewModel viewModel = new EuInviteViewModel(euRegistrants.getContent(),
                                                            new Pagination(euRegistrants, ""),
                                                            1200,
                                                            2100);
        model.addAttribute("model", viewModel);
        return "eu/notified";
    }

    @PostMapping("/eu-send-invites")
    public String sendEuInvites(EuContactSelectionForm euContactSelectionForm) {
        List<Long> ids = euContactSelectionForm.getEuContactIds();
        euInviteRestService.sendInvites(ids).getSuccess();

        return "redirect:/dashboard";
    }
}