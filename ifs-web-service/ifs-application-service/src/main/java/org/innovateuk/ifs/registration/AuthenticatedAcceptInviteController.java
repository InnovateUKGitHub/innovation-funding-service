package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.invite.service.InviteService;
import org.innovateuk.ifs.invite.service.InviteServiceImpl;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.registration.service.RegistrationService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.service.InviteServiceImpl.INVITE_ALREADY_ACCEPTED;


/**
 * This class is use as an entry point to accept a invite, to a application.
 */
@Controller
@PreAuthorize("hasAuthority('applicant')")
public class AuthenticatedAcceptInviteController extends AbstractAcceptInviteController {
    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private InviteService inviteService;
    private static final String INVITE_FOR_DIFFERENT_ORGANISATION_THAN_USERS_VIEW = "TODO";
    private static final String INVITE_FOR_DIFFERENT_ORGANISATION_THAN_USERS_BUT_SAME_NAME_VIEW = "TODO";

    private String inviteForDifferentOrganisationThanUsers(HttpServletResponse response) {
        clearDownInviteFlowCookies(response);
        return INVITE_FOR_DIFFERENT_ORGANISATION_THAN_USERS_VIEW;
    }

    private String inviteForDifferentOrganisationThanUsersButSameName(HttpServletResponse response) {
        clearDownInviteFlowCookies(response);
        return INVITE_FOR_DIFFERENT_ORGANISATION_THAN_USERS_BUT_SAME_NAME_VIEW;
    }

    @RequestMapping(value = "/accept-invite-authenticated/confirm-invited-organisation", method = RequestMethod.GET)
    public String confirmInvite(HttpServletResponse response,
                                HttpServletRequest request,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                Model model) {
        String hash = getInviteHashCookie(request);
        RestResult<String> view = inviteRestService.getInviteByHash(getInviteHashCookie(request)).andOnSuccessReturn(invite -> {
            if (!SENT.equals(invite.getStatus())) {
                return restSuccess(alreadyAcceptedView(response));
            }
            return inviteRestService.getInviteOrganisationByHash(hash).andOnSuccessReturn(inviteOrganisation -> {
                        if (loggedInAsNonInviteUser(invite, loggedInUser)) {
                            return LOGGED_IN_WITH_ANOTHER_USER_VIEW;
                        } else if (registrationService.isInviteForDifferentOrganisationThanUsers(invite, inviteOrganisation)) {
                            return inviteForDifferentOrganisationThanUsers(response);
                        } else if (registrationService.isInviteForDifferentOrganisationThanUsersButSameName(invite, inviteOrganisation)) {
                            return inviteForDifferentOrganisationThanUsersButSameName(response);
                        }
                        // Success
                        OrganisationResource organisation = getInviteOrganisationOrElseUserOrganisation(loggedInUser, inviteOrganisation);
                        model.addAttribute("invite", invite);
                        model.addAttribute("organisation", organisation);
                        model.addAttribute("organisationAddress", getOrganisationAddress(organisation));
                        model.addAttribute("acceptInviteUrl", "/accept-invite-authenticated/confirm-invited-organisation/confirm");
                        return "registration/confirm-registered-organisation";
                    }
            );
        }).andOnFailure(clearDownInviteFlowCookiesFn(response));
        return view.getSuccessObjectOrThrowException();
    }

    @RequestMapping(value = "/accept-invite-authenticated/confirm-invited-organisation/confirm", method = RequestMethod.GET)
    public String confirmedInvite(HttpServletResponse response, HttpServletRequest request, Model model) {
        UserResource loggedInUser = userAuthenticationService.getAuthenticatedUser(request);

        ApplicationInviteResource inviteResource = inviteService.getInviteByRequest(request, response);
        if (SENT.equals(inviteResource.getStatus())) {
            InviteOrganisationResource inviteOrganisation = inviteRestService.getInviteOrganisationByHash(inviteResource.getHash()).getSuccessObjectOrThrowException();

            Map<String, String> failureMessages = registrationService.getInvalidInviteMessages(loggedInUser, inviteResource, inviteOrganisation);

            if (failureMessages.size() > 0) {
                failureMessages.forEach((messageKey, messageValue) -> model.addAttribute(messageKey, messageValue));

                return "registration/accept-invite-failure";
            }
            inviteRestService.acceptInvite(inviteResource.getHash(), loggedInUser.getId()).getSuccessObjectOrThrowException();
            cookieUtil.removeCookie(response, InviteServiceImpl.INVITE_HASH);
            return "redirect:/application/" + inviteResource.getApplication();

        } else {
            return "redirect:/login";
        }
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

    /**
     * Get the most import address of the organisation. If there is a operating address, use that otherwise just get the first one.
     */
    private AddressResource getOrganisationAddress(OrganisationResource organisation) {
        AddressResource address = null;
        if (organisation.getAddresses().size() == 1) {
            address = organisation.getAddresses().get(0).getAddress();
        } else if (!organisation.getAddresses().isEmpty()) {
            Optional<OrganisationAddressResource> addressOptional = organisation.getAddresses().stream().filter(a -> OrganisationAddressType.OPERATING.equals(OrganisationAddressType.valueOf(a.getAddressType().getName()))).findAny();
            if (addressOptional.isPresent()) {
                address = addressOptional.get().getAddress();
            } else {
                address = organisation.getAddresses().get(0).getAddress();
            }
        }
        return address;
    }

}
