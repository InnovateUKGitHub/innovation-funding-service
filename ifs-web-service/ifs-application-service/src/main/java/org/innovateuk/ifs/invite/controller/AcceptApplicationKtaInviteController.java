package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.populator.AcceptRejectApplicationKtaInviteModelPopulator;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.invite.service.ApplicationKtaInviteRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;

import static org.innovateuk.ifs.exception.ErrorControllerAdvice.URL_HASH_INVALID_TEMPLATE;

@Controller
@SecuredBySpring(value = "Controller",
        description = "All KTA users with a valid invite hash are able to view and accept the corresponding invite",
        securedType = AcceptApplicationKtaInviteController.class)
public class AcceptApplicationKtaInviteController extends AbstractAcceptInviteController {

    @Autowired
    private ApplicationKtaInviteRestService ktaInviteRestService;

    @Autowired
    private AcceptRejectApplicationKtaInviteModelPopulator acceptRejectApplicationKtaInviteModelPopulator;

    @GetMapping("/kta/accept-invite/{hash}")
    @SecuredBySpring(value = "READ", description = "All users can view the invite to join an application.")
    @PreAuthorize("permitAll")
    public String inviteEntryPage(
            @PathVariable("hash") final String hash,
            UserResource loggedInUser,
            HttpServletResponse response,
            Model model) {

        RestResult<ApplicationKtaInviteResource> invite = ktaInviteRestService.getKtaInviteByHash(hash);
        if (invite.isSuccess()) {
            if (!InviteStatus.SENT.equals(invite.getSuccess().getStatus())) {
                return alreadyAcceptedView(response);
            }
            if (loggedInAsNonKtaInviteUser(invite.getSuccess(), loggedInUser)) {
                return LOGGED_IN_WITH_ANOTHER_USER_VIEW;
            }
            model.addAttribute("model", acceptRejectApplicationKtaInviteModelPopulator.populateModel(invite.getSuccess()));
            return "registration/accept-invite-kta-user";
        } else {
            return URL_HASH_INVALID_TEMPLATE;
        }
    }

    @GetMapping("/kta/accept-invite/{hash}/accept")
    @PreAuthorize("hasAuthority('knowledge_transfer_adviser')")
    @SecuredBySpring(value = "ACCEPT", description = "KTA users can accept invite to join an application.")
    public String acceptKtaPage(
            @PathVariable("hash") final String hash,
            UserResource loggedInUser,
            HttpServletResponse response) {

        RestResult<ApplicationKtaInviteResource> invite = ktaInviteRestService.getKtaInviteByHash(hash);
        if (invite.isSuccess()) {
            if (!InviteStatus.SENT.equals(invite.getSuccess().getStatus())) {
                return alreadyAcceptedView(response);
            }
            if (loggedInAsNonKtaInviteUser(invite.getSuccess(), loggedInUser)) {
                return LOGGED_IN_WITH_ANOTHER_USER_VIEW;
            }
            ktaInviteRestService.acceptInvite(hash).getSuccess();
            return "redirect:/";
        } else {
            return URL_HASH_INVALID_TEMPLATE;
        }
    }

    private boolean loggedInAsNonKtaInviteUser(ApplicationKtaInviteResource invite, UserResource loggedInUser) {
        return loggedInUser != null && !invite.getEmail().equalsIgnoreCase(loggedInUser.getEmail());
    }
}