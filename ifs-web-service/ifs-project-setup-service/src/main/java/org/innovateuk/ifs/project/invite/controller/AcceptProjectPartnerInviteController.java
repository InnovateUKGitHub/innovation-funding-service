package org.innovateuk.ifs.project.invite.controller;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.service.ProjectPartnerInviteRestService;
import org.innovateuk.ifs.registration.form.InviteAndIdCookie;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;

@Controller
@RequestMapping("/project/{projectId}/partner-invite")
@SecuredBySpring(value = "Controller",
        description = "All users with a valid invite hash are able to view and accept the corresponding invite",
        securedType = AcceptProjectPartnerInviteController.class)
@PreAuthorize("permitAll")
public class AcceptProjectPartnerInviteController {

    @Autowired
    private RegistrationCookieService registrationCookieService;

    @Autowired
    private ProjectPartnerInviteRestService projectPartnerInviteRestService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @GetMapping("/{hash}/accept")
    public String inviteEntryPage(
            @PathVariable long projectId,
            @PathVariable String hash,
            UserResource loggedInUser,
            HttpServletResponse response) {

        registrationCookieService.deleteAllRegistrationJourneyCookies(response);

        return projectPartnerInviteRestService.getInviteByHash(projectId, hash).andOnSuccessReturn(invite -> {
            if (!SENT.equals(invite.getStatus())) {
                return alreadyAcceptedView(response);
            }
            if (loggedInAsNonInviteUser(invite, loggedInUser)) {
                return "registration/logged-in-with-another-user-failure";
            }
            registrationCookieService.saveToProjectInviteHashCookie(new InviteAndIdCookie(projectId, hash), response);
            if (invite.getExistingUser() == null) {
                return String.format("redirect:/project/%d/partner-invite/new-user", projectId);
            } else {
                return String.format("redirect:/project/%d/partner-invite/existing-user", projectId);
            }
        }).getSuccess();
    }

    @GetMapping("/new-user")
    public String newUserInvitePage(@PathVariable long projectId,
                                    HttpServletRequest request,
                                    Model model) {
        return registrationCookieService.getProjectInviteHashCookieValue(request).map(cookie ->
                projectPartnerInviteRestService.getInviteByHash(projectId, cookie.getHash()).andOnSuccessReturn(invite -> {
                    model.addAttribute("projectName", invite.getProjectName());
                    return "project/partner-invite/new-user";
            }).getSuccess()
        ).orElseThrow(ObjectNotFoundException::new);
    }

    private String alreadyAcceptedView(HttpServletResponse response) {
        cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");
        return "redirect:/login";
    }

    private final boolean loggedInAsNonInviteUser(SentProjectPartnerInviteResource invite, UserResource loggedInUser) {
        if (loggedInUser == null || invite.getEmail().equalsIgnoreCase(loggedInUser.getEmail())){
            return false;
        }
        return true;
    }
}
