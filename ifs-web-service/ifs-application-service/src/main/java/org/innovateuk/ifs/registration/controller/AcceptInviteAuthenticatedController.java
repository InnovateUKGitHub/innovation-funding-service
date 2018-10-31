package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.registration.populator.ConfirmOrganisationInviteModelPopulator;
import org.innovateuk.ifs.registration.viewmodel.ConfirmOrganisationInviteOrganisationViewModel;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;


/**
 * This class is use as an entry point to accept a invite, to a application.
 */
@Controller
@SecuredBySpring(value="Controller", description = "Applicants and assessors can accept invites to an application." +
        " Assessors will become applicants by accepting", securedType = AcceptInviteAuthenticatedController.class)
@PreAuthorize("hasAnyAuthority('applicant', 'assessor')")
public class AcceptInviteAuthenticatedController extends AbstractAcceptInviteController {

    private InviteRestService inviteRestService;
    private OrganisationRestService organisationRestService;
    private ConfirmOrganisationInviteModelPopulator confirmOrganisationInviteModelPopulator;
    private UserRestService userRestService;
    private CookieUtil cookieUtil;

    public AcceptInviteAuthenticatedController(final InviteRestService inviteRestService,
                                               final OrganisationRestService organisationRestService,
                                               final ConfirmOrganisationInviteModelPopulator confirmOrganisationInviteModelPopulator,
                                               final UserRestService userRestService,
                                               final CookieUtil cookieUtil) {
        this.inviteRestService = inviteRestService;
        this.organisationRestService = organisationRestService;
        this.confirmOrganisationInviteModelPopulator = confirmOrganisationInviteModelPopulator;
        this.userRestService = userRestService;
        this.cookieUtil = cookieUtil;
    }

    @GetMapping("/accept-invite-authenticated/confirm-invited-organisation")
    public String existingUserAndOrganisation(HttpServletResponse response,
                                              HttpServletRequest request,
                                              UserResource loggedInUser,
                                              Model model) {
        String hash = registrationCookieService.getInviteHashCookieValue(request).orElse(null);
        RestResult<String> view = inviteRestService.getInviteByHash(hash).andOnSuccess(invite ->
                inviteRestService.getInviteOrganisationByHash(hash).andOnSuccessReturn(inviteOrganisation -> {
                    String validateView = validate(invite, response, loggedInUser, model);
                    if (validateView != null) {
                        return validateView;
                    }
                    // Success
                    OrganisationResource organisation =
                            organisationRestService.getOrganisationById(inviteOrganisation.getOrganisation()).getSuccess();
                    String registerUrl = "/accept-invite-authenticated/confirm-invited-organisation/confirm";
                    ConfirmOrganisationInviteOrganisationViewModel viewModel =
                            confirmOrganisationInviteModelPopulator.populate(invite, organisation, registerUrl);
                    model.addAttribute("model", viewModel);
                    return "registration/confirm-registered-organisation";
                })
        ).andOnFailure(clearDownInviteFlowCookiesFn(response));
        return view.getSuccess();
    }

    @GetMapping("/accept-invite-authenticated/confirm-invited-organisation/confirm")
    public String confirmExistingUserAndOrganisation(HttpServletResponse response,
                                  HttpServletRequest request,
                                  UserResource loggedInUser,
                                  Model model) {
        String hash = registrationCookieService.getInviteHashCookieValue(request).orElse(null);
        RestResult<String> view = inviteRestService.getInviteByHash(hash).andOnSuccessReturn(invite -> {
                    String validateView = validate(invite, response, loggedInUser, model);
                    if (validateView != null) {
                        return validateView;
                    }

                    if (!loggedInUser.hasRole(Role.APPLICANT)) {
                        userRestService.grantRole(loggedInUser.getId(), Role.APPLICANT).getSuccess();
                        cookieUtil.saveToCookie(response, "role", Role.APPLICANT.getName());
                    }
                    // Success
                    inviteRestService.acceptInvite(invite.getHash(), loggedInUser.getId()).getSuccess();
                    clearDownInviteFlowCookies(response);
                    return "redirect:/application/" + invite.getApplication();
                }
        ).andOnFailure(clearDownInviteFlowCookiesFn(response));
        return view.getSuccess();
    }

    @GetMapping("/accept-invite-authenticated/confirm-new-organisation")
    public String existingUserAndNewOrganisation(HttpServletResponse response,
                                    HttpServletRequest request,
                                    UserResource loggedInUser,
                                    Model model) {
        String hash = registrationCookieService.getInviteHashCookieValue(request).orElse(null);
        RestResult<String> view = inviteRestService.getInviteByHash(hash).andOnSuccessReturn(invite -> {
                    String validateView = validate(invite, response, loggedInUser, model);
                    if (validateView != null) {
                        return validateView;
                    }

                    return "redirect:/organisation/select";
                }
        ).andOnFailure(clearDownInviteFlowCookiesFn(response));
        return view.getSuccess();
    }

    private String validate(ApplicationInviteResource invite, HttpServletResponse response, UserResource loggedInUser, Model model) {
        if (!SENT.equals(invite.getStatus())) {
            return alreadyAcceptedView(response);
        }
        if (loggedInAsNonInviteUser(invite, loggedInUser)) {
            return LOGGED_IN_WITH_ANOTHER_USER_VIEW;
        }
        return null;
    }
}
