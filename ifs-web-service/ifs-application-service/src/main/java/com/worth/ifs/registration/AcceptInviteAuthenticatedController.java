package com.worth.ifs.registration;

import com.worth.ifs.BaseController;
import com.worth.ifs.address.resource.AddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.commons.error.exception.InvalidURLException;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.service.InviteOrganisationRestService;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.OrganisationTypeRestService;
import com.worth.ifs.user.service.UserService;
import com.worth.ifs.util.CookieUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * This class is use as an entry point to accept a invite, to a application.
 */
@Controller
public class AcceptInviteAuthenticatedController extends BaseController{
    public static final String INVITE_HASH = "invite_hash";
    private static final Log LOG = LogFactory.getLog(AcceptInviteAuthenticatedController.class);
    @Autowired
    private InviteRestService inviteRestService;
    @Autowired
    private InviteOrganisationRestService inviteOrganisationRestService;
    @Autowired
    private UserService userService;
    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    UserAuthenticationService userAuthenticationService;

    @RequestMapping(value = "/accept-invite-authenticated/confirm-invited-organisation", method = RequestMethod.GET)
    public String confirmInvite(HttpServletResponse response, HttpServletRequest request, Model model) {
        UserResource loggedInUser = userAuthenticationService.getAuthenticatedUser(request);

        String hash = CookieUtil.getCookieValue(request, INVITE_HASH);
        RestResult<InviteResource> invite = inviteRestService.getInviteByHash(hash);

        if (invite.isSuccess()) {
            InviteResource inviteResource = invite.getSuccessObject();
            if (InviteStatusConstants.SEND.equals(inviteResource.getStatus())) {
                InviteOrganisationResource inviteOrganisation = inviteRestService.getInviteOrganisationByHash(hash).getSuccessObjectOrThrowException();

                if (invalidInvite(model, loggedInUser, inviteResource, inviteOrganisation)){
                    return "registration/accept-invite-failure";
                }
                OrganisationResource organisation = getUserOrInviteOrganisation(loggedInUser, inviteOrganisation);

                model.addAttribute("invite", inviteResource);
                model.addAttribute("organisation", organisation);
                model.addAttribute("organisationAddress", getOrganisationAddress(organisation));
                model.addAttribute("acceptInviteUrl", "/accept-invite-authenticated/confirm-invited-organisation/confirm");
                return "registration/confirm-registered-organisation";
            } else {
                CookieUtil.removeCookie(response, INVITE_HASH);
                cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");
                return "redirect:/login";
            }
        } else {
            CookieUtil.removeCookie(response, INVITE_HASH);
            throw new InvalidURLException("Invite url is not valid", null);
        }
    }

    @RequestMapping(value = "/accept-invite-authenticated/confirm-invited-organisation/confirm", method = RequestMethod.GET)
    public String confirmedInvite(HttpServletResponse response, HttpServletRequest request, Model model) {
        UserResource loggedInUser = userAuthenticationService.getAuthenticatedUser(request);

        String hash = CookieUtil.getCookieValue(request, INVITE_HASH);
        RestResult<InviteResource> invite = inviteRestService.getInviteByHash(hash);

        if (invite.isSuccess()) {
            InviteResource inviteResource = invite.getSuccessObject();
            if (InviteStatusConstants.SEND.equals(inviteResource.getStatus())) {
                InviteOrganisationResource inviteOrganisation = inviteRestService.getInviteOrganisationByHash(hash).getSuccessObjectOrThrowException();

                if (invalidInvite(model, loggedInUser, inviteResource, inviteOrganisation)) {
                    return "registration/accept-invite-failure";
                }
                inviteRestService.acceptInvite(hash, loggedInUser.getId()).getSuccessObjectOrThrowException();
                CookieUtil.removeCookie(response, AcceptInviteController.INVITE_HASH);
                return "redirect:/application/"+ inviteResource.getApplication();

            } else {
                return AcceptInviteController.handleAcceptedInvite(cookieFlashMessageFilter, response);
            }
        } else {
            AcceptInviteController.handleInvalidInvite(response);
        }
        return "";
    }

    private OrganisationResource getUserOrInviteOrganisation(UserResource loggedInUser, InviteOrganisationResource inviteOrganisation) {
        OrganisationResource organisation;
        if(inviteOrganisation.getOrganisation() == null){
            // no one has confirmed the InviteOrganisation, we can use the users organisation.
            organisation = organisationService.getOrganisationById(loggedInUser.getOrganisations().get(0));
        }else{
            organisation = organisationService.getOrganisationById(inviteOrganisation.getOrganisation());
        }
        return organisation;
    }

    public static boolean invalidInvite(Model model, UserResource loggedInUser, InviteResource inviteResource, InviteOrganisationResource inviteOrganisation) {
        if (!inviteResource.getEmail().equals(loggedInUser.getEmail())) {
            // Invite is for different emailaddress then current logged in user.
            model.addAttribute("failureMessageKey", "registration.LOGGED_IN_WITH_OTHER_ACCOUNT");
            return true;
        } else if (inviteOrganisation.getOrganisation() != null && !inviteOrganisation.getOrganisation().equals(loggedInUser.getOrganisations().get(0))) {
            // Invite Organisation is already confirmed, with different organisation than the current users organisation.
            model.addAttribute("failureMessageKey", "registration.MULTIPLE_ORGANISATIONS");
            return true;
        }
        return false;
    }

    /**
     * Get the most import address of the organisation. If there is a operating address, use that otherwise just get the first one.
     */
    private AddressResource getOrganisationAddress(OrganisationResource organisation) {
        AddressResource address = null;
        if (organisation.getAddresses().size() == 1) {
            address = organisation.getAddresses().get(0).getAddress();
        } else if (!organisation.getAddresses().isEmpty()) {
            Optional<OrganisationAddressResource> addressOptional = organisation.getAddresses().stream().filter(a -> AddressType.OPERATING.equals(a.getAddressType())).findAny();
            if (addressOptional.isPresent()) {
                address = addressOptional.get().getAddress();
            } else {
                address = organisation.getAddresses().get(0).getAddress();
            }
        }
        return address;
    }

}
