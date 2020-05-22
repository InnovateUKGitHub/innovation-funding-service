package org.innovateuk.ifs.project.invite.controller;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.service.ProjectPartnerInviteRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.registration.form.InviteAndIdCookie;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.NavigationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.lang.String.format;
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
    @Autowired
    private NavigationUtils navigationUtils;
    @Autowired
    private ProjectRestService projectRestService;
    @Autowired
    private CompetitionOrganisationConfigRestService organisationConfigRestService;

    @GetMapping("/{hash}/accept")
    public String inviteEntryPage(
            @PathVariable long projectId,
            @PathVariable String hash,
            UserResource loggedInUser,
            HttpServletResponse response,
            HttpServletRequest request) {

        registrationCookieService.deleteAllRegistrationJourneyCookies(response);

        return projectPartnerInviteRestService.getInviteByHash(projectId, hash).andOnSuccessReturn(invite -> {
            if (invite.getStatus() != SENT) {
                return alreadyAcceptedView(response, request);
            }
            if (loggedInAsNonInviteUser(invite, loggedInUser)) {
                return "registration/logged-in-with-another-user-failure";
            }
            registrationCookieService.saveToProjectInviteHashCookie(new InviteAndIdCookie(projectId, hash), response);
            if (invite.getUser() == null) {
                return format("redirect:/project/%d/partner-invite/new-user", projectId);
            } else {
                return format("redirect:/project/%d/partner-invite/existing-user", projectId);
            }
        }).getSuccess();
    }

    @GetMapping("/new-user")
    public String newUserInvitePage(@PathVariable long projectId,
                                    HttpServletRequest request,
                                    Model model) {
        return registrationCookieService.getProjectInviteHashCookieValue(request).map(cookie ->
                projectPartnerInviteRestService.getInviteByHash(projectId, cookie.getHash()).andOnSuccessReturn(invite -> {

                    ProjectResource projectResource = projectRestService.getProjectById(projectId).getSuccess();
                    CompetitionOrganisationConfigResource organisationConfigResource = organisationConfigRestService.findByCompetitionId(projectResource.getCompetition()).getSuccess();
                    boolean international = organisationConfigResource.getInternationalOrganisationsAllowed();

                    model.addAttribute("projectName", invite.getProjectName());
                    model.addAttribute("internationalCompetition", international);
                    return "project/partner-invite/new-user";
            }).getSuccess()
        ).orElseThrow(ObjectNotFoundException::new);
    }

    @GetMapping("/existing-user")
    public String existingUserPage(@PathVariable long projectId,
                                    HttpServletRequest request,
                                    UserResource user,
                                    Model model) {
        return registrationCookieService.getProjectInviteHashCookieValue(request).map(cookie ->
                projectPartnerInviteRestService.getInviteByHash(projectId, cookie.getHash()).andOnSuccessReturn(invite -> {
                    ProjectResource projectResource = projectRestService.getProjectById(projectId).getSuccess();
                    CompetitionOrganisationConfigResource organisationConfigResource = organisationConfigRestService.findByCompetitionId(projectResource.getCompetition()).getSuccess();
                    boolean international = organisationConfigResource.getInternationalOrganisationsAllowed();

                    model.addAttribute("projectName", invite.getProjectName());
                    model.addAttribute("loggedIn", user != null);
                    model.addAttribute("projectId", projectId);
                    model.addAttribute("internationalCompetition", international);

                    return "project/partner-invite/existing-user";
                }).getSuccess()
        ).orElseThrow(ObjectNotFoundException::new);
    }

    @GetMapping("/authenticate")
    @PreAuthorize("isAuthenticated()")
    @SecuredBySpring(value = "FORCE_LOGIN_FOR_EXISTING_USER_INVITE",
            description = "User must be logged into account to accept invite for an existing user.")
    public String forceLogin(@PathVariable long projectId,
                                   HttpServletRequest request,
                                   UserResource user,
                                   Model model) {
        return registrationCookieService.getProjectInviteHashCookieValue(request).map(cookie ->
                projectPartnerInviteRestService.getInviteByHash(projectId, cookie.getHash()).andOnSuccessReturn(invite -> {
                    //Force user to be logged in.
                    if (loggedInAsNonInviteUser(invite, user)) {
                        return "registration/logged-in-with-another-user-failure";
                    }

                    return navigationUtils.getRedirectToSameDomainUrl(request, "organisation/select");
                }).getSuccess()
        ).orElseThrow(ObjectNotFoundException::new);
    }

    private String alreadyAcceptedView(HttpServletResponse response, HttpServletRequest request) {
        cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");
        return navigationUtils.getRedirectToSameDomainUrl(request, "not-found");
    }

    private boolean loggedInAsNonInviteUser(SentProjectPartnerInviteResource invite, UserResource loggedInUser) {
        if (loggedInUser == null || invite.getEmail().equalsIgnoreCase(loggedInUser.getEmail())){
            return false;
        }
        return true;
    }
}
