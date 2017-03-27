package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.registration.model.InviteAndUserOrganisationDifferentModelPopulator;
import org.innovateuk.ifs.registration.service.RegistrationService;
import org.innovateuk.ifs.registration.viewmodel.ConfirmOrganisationInviteOrganisationViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;


/**
 * This class is use as an entry point to accept a invite, to a application.
 */
@Controller
@PreAuthorize("hasAuthority('applicant')")
public class AcceptInviteAuthenticatedController extends AbstractAcceptInviteController {
    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private InviteAndUserOrganisationDifferentModelPopulator inviteAndUserOrganisationDifferentModelPopulator;

    private static final String INVITE_FOR_DIFFERENT_ORGANISATION_THAN_USERS_VIEW = "registration/invite-for-different-organisation-than-users";
    private static final String INVITE_FOR_DIFFERENT_ORGANISATION_THAN_USERS_BUT_SAME_NAME_VIEW = "registration/invite-for-different-organisation-than-users-but-same-name";


    @RequestMapping(value = "/accept-invite-authenticated/confirm-invited-organisation", method = RequestMethod.GET)
    public String confirmInvite(HttpServletResponse response,
                                HttpServletRequest request,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                Model model) {
        String hash = getInviteHashCookie(request);
        RestResult<String> view = inviteRestService.getInviteByHash(getInviteHashCookie(request)).andOnSuccess(invite ->
                inviteRestService.getInviteOrganisationByHash(hash).andOnSuccessReturn(inviteOrganisation -> {
                            String validateView = validate(invite, inviteOrganisation, response, loggedInUser, model);
                            if (validateView != null) {
                                return validateView;
                            }
                            // Success
                            OrganisationResource organisation = getInviteOrganisationOrElseUserOrganisation(loggedInUser, inviteOrganisation);
                            model.addAttribute("model",
                                    new ConfirmOrganisationInviteOrganisationViewModel(invite, organisation, getOrganisationAddress(organisation),
                                            "/accept-invite-authenticated/confirm-invited-organisation/confirm"));
                            return "registration/confirm-registered-organisation";
                        }
                )
        ).andOnFailure(clearDownInviteFlowCookiesFn(response));
        return view.getSuccessObject();
    }

    @RequestMapping(value = "/accept-invite-authenticated/confirm-invited-organisation/confirm", method = RequestMethod.GET)
    public String confirmedInvite(HttpServletResponse response,
                                  HttpServletRequest request,
                                  @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                  Model model) {
        String hash = getInviteHashCookie(request);
        RestResult<String> view = inviteRestService.getInviteByHash(getInviteHashCookie(request)).andOnSuccess(invite ->
                inviteRestService.getInviteOrganisationByHash(hash).andOnSuccessReturn(inviteOrganisation -> {
                    String validateView = validate(invite, inviteOrganisation, response, loggedInUser, model);
                    if (validateView != null) {
                        return validateView;
                    }
                    // Success
                    inviteRestService.acceptInvite(invite.getHash(), loggedInUser.getId()).getSuccessObjectOrThrowException();
                    clearDownInviteFlowCookies(response);
                    return "redirect:/application/" + invite.getApplication();
                })
        ).andOnFailure(clearDownInviteFlowCookiesFn(response));
        return view.getSuccessObject();
    }

    private OrganisationResource getInviteOrganisationOrElseUserOrganisation(UserResource loggedInUser, InviteOrganisationResource inviteOrganisation) {
        if (inviteOrganisation.getOrganisation() == null) {
            // No one has confirmed the InviteOrganisation, we can use the users Organisation.
            // Note that this makes the assumption that the user will have an organisation
            return organisationService.getOrganisationForUser(loggedInUser.getId());
        } else {
            return organisationService.getOrganisationById(inviteOrganisation.getOrganisation());
        }
    }

    private String validate(ApplicationInviteResource invite, InviteOrganisationResource inviteOrganisation, HttpServletResponse response, UserResource loggedInUser, Model model) {
        if (!SENT.equals(invite.getStatus())) {
            return alreadyAcceptedView(response);
        }
        if (loggedInAsNonInviteUser(invite, loggedInUser)) {
            return LOGGED_IN_WITH_ANOTHER_USER_VIEW;
        } else if (registrationService.isInviteForDifferentOrganisationThanUsersAndDifferentName(invite, inviteOrganisation)) {
            return inviteForDifferentOrganisationThanUsers(response, model, invite);
        } else if (registrationService.isInviteForDifferentOrganisationThanUsersButSameName(invite, inviteOrganisation)) {
            return inviteForDifferentOrganisationThanUsersButSameName(response, model, invite);
        }
        return null;
    }

    private String inviteForDifferentOrganisationThanUsers(HttpServletResponse response, Model model, ApplicationInviteResource invite) {
        clearDownInviteFlowCookies(response);
        model.addAttribute("model", inviteAndUserOrganisationDifferentModelPopulator.populateModel(invite));
        return INVITE_FOR_DIFFERENT_ORGANISATION_THAN_USERS_VIEW;
    }

    private String inviteForDifferentOrganisationThanUsersButSameName(HttpServletResponse response, Model model, ApplicationInviteResource invite) {
        clearDownInviteFlowCookies(response);
        model.addAttribute("model", inviteAndUserOrganisationDifferentModelPopulator.populateModel(invite));
        return INVITE_FOR_DIFFERENT_ORGANISATION_THAN_USERS_BUT_SAME_NAME_VIEW;
    }

}
