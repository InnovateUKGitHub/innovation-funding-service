package org.innovateuk.ifs.eu.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;

import org.innovateuk.ifs.eu.form.EuGrantSelectionForm;
import org.innovateuk.ifs.eu.invite.EuInviteRestService;
import org.innovateuk.ifs.eu.viewmodel.EuInviteViewModel;
import org.innovateuk.ifs.eugrant.EuGrantPageResource;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


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
                                               @RequestParam(value = "numSentEmails", required = false) Optional<Long> successfulEmailsSent,
                                               EuGrantSelectionForm form,
                                               Model model) {
        EuGrantPageResource euRegistrants = euInviteRestService.getEuGrantsByNotified(false,
                                                                                      pageIndex,
                                                                                      DEFAULT_PAGE_SIZE).getSuccess();
        long totalSubmitted = euInviteRestService.getTotalSubmittedEuGrants().getSuccess();
        long totalNonNotified = euRegistrants.getTotalElements();

        EuInviteViewModel viewModel = new EuInviteViewModel(euRegistrants.getContent(),
                                                            new Pagination(euRegistrants, ""),
                                                            totalSubmitted - totalNonNotified,
                                                            totalNonNotified,
                                                            successfulEmailsSent.isPresent(),
                                                            successfulEmailsSent.orElse(0L));
        model.addAttribute("model", viewModel);
        return "eu/non-notified";
    }

    @GetMapping("/eu-invite-notified")
    public String viewNotifiedEuRegistrants(@RequestParam(value = "page", defaultValue = "0") int pageIndex,
                                            @RequestParam(value = "numSentEmails", required = false) Optional<Long> successfulEmailsSent,
                                            EuGrantSelectionForm form,
                                            Model model) {
        EuGrantPageResource euRegistrants = euInviteRestService.getEuGrantsByNotified(true,
                                                                                      pageIndex,
                                                                                      DEFAULT_PAGE_SIZE).getSuccess();

        long totalSubmitted = euInviteRestService.getTotalSubmittedEuGrants().getSuccess();
        long totalNotified = euRegistrants.getTotalElements();
        EuInviteViewModel viewModel = new EuInviteViewModel(euRegistrants.getContent(),
                                                            new Pagination(euRegistrants, ""),
                                                            totalNotified,
                                                            totalSubmitted - totalNotified,
                                                            successfulEmailsSent.isPresent(),
                                                            successfulEmailsSent.orElse(0L));
        model.addAttribute("model", viewModel);
        return "eu/notified";
    }

    @PostMapping("/eu-send-invites/notified/{notified}")
    public String sendEuInvites(@PathVariable("notified") boolean notified,
                                EuGrantSelectionForm euGrantSelectionForm) {
        List<UUID> ids = euGrantSelectionForm.getEuGrantIds();
        euInviteRestService.sendInvites(ids).getSuccess();

        return redirectWithEmailParams(notified, ids.size());
    }

    private String redirectWithEmailParams(boolean notified, long numEmails) {

        String baseUrl = notified? "/eu-invite-notified" : "/eu-invite-non-notified";
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl);
        builder.queryParam("numSentEmails", numEmails);
        return "redirect:" + builder.toUriString();
    }
}