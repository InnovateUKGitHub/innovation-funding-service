package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.BaseController;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.exception.InvalidURLException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.invite.service.InviteService;
import org.innovateuk.ifs.invite.service.InviteServiceImpl;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.registration.service.RegistrationService;
import org.innovateuk.ifs.registration.viewmodel.ConfirmOrganisationInviteOrganisation;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;


/**
 * This class is use as an entry point to accept a invite, to a application.
 */
@Controller
@PreAuthorize("hasAuthority('applicant')")
public class AcceptInviteAuthenticatedController extends BaseController{
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

    @RequestMapping(value = "/accept-invite-authenticated/confirm-invited-organisation", method = RequestMethod.GET)
    public String confirmInvite(HttpServletResponse response, HttpServletRequest request, Model model) {
        UserResource loggedInUser = userAuthenticationService.getAuthenticatedUser(request);

        String hash = cookieUtil.getCookieValue(request, InviteServiceImpl.INVITE_HASH);
        RestResult<ApplicationInviteResource> invite = inviteRestService.getInviteByHash(hash);

        if (invite.isSuccess()) {
            ApplicationInviteResource inviteResource = invite.getSuccessObject();
            if (InviteStatus.SENT.equals(inviteResource.getStatus())) {
                InviteOrganisationResource inviteOrganisation = inviteRestService.getInviteOrganisationByHash(hash).getSuccessObjectOrThrowException();

                Map<String, String> failureMessages = registrationService.getInvalidInviteMessages(loggedInUser, inviteResource, inviteOrganisation);

                if (failureMessages.size() > 0){
                    failureMessages.forEach((messageKey, messageValue) -> model.addAttribute(messageKey, messageValue));
                    return "registration/accept-invite-failure";
                }
                OrganisationResource organisation = getUserOrInviteOrganisation(loggedInUser, inviteOrganisation);

                model.addAttribute("model",
                        new ConfirmOrganisationInviteOrganisation(inviteResource, organisation, getOrganisationAddress(organisation),
                        "/accept-invite-authenticated/confirm-invited-organisation/confirm"));
                return "registration/confirm-registered-organisation";
            } else {
                cookieUtil.removeCookie(response, InviteServiceImpl.INVITE_HASH);
                cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");
                return "redirect:/login";
            }
        } else {
            cookieUtil.removeCookie(response, InviteServiceImpl.INVITE_HASH);
            throw new InvalidURLException("Invite url is not valid", null);
        }
    }

    @RequestMapping(value = "/accept-invite-authenticated/confirm-invited-organisation/confirm", method = RequestMethod.GET)
    public String confirmedInvite(HttpServletResponse response, HttpServletRequest request, Model model) {
        UserResource loggedInUser = userAuthenticationService.getAuthenticatedUser(request);

        ApplicationInviteResource inviteResource = inviteService.getInviteByRequest(request, response);
        if (InviteStatus.SENT.equals(inviteResource.getStatus())) {
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

    private OrganisationResource getUserOrInviteOrganisation(UserResource loggedInUser, InviteOrganisationResource inviteOrganisation) {
        OrganisationResource organisation;
        if(inviteOrganisation.getOrganisation() == null){
            // no one has confirmed the InviteOrganisation, we can use the users organisation.
            organisation = organisationService.getOrganisationForUser(loggedInUser.getId());
        }else{
            organisation = organisationService.getOrganisationById(inviteOrganisation.getOrganisation());
        }
        return organisation;
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
