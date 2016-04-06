package com.worth.ifs.application;

import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.commons.error.exception.InvalidURLException;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.service.InviteOrganisationRestService;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.registration.OrganisationCreationController;
import com.worth.ifs.registration.RegistrationController;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.service.OrganisationTypeRestService;
import com.worth.ifs.user.service.UserService;
import com.worth.ifs.util.CookieUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * This class is use as an entry point to accept a invite, to a application.
 */
@Controller
public class AcceptInviteController {
    public static final String INVITE_HASH = "invite_hash";
    public static final String ORGANISATION_TYPE = "organisationType";
    private static final Log LOG = LogFactory.getLog(AcceptInviteController.class);
    Validator validator;
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
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @RequestMapping(value = "/accept-invite/{hash}", method = RequestMethod.GET)
    public String inviteEntryPage(
            @PathVariable("hash") final String hash,
            HttpServletResponse response,
            Model model) {
        RestResult<InviteResource> invite = inviteRestService.getInviteByHash(hash);
        CookieUtil.removeCookie(response, OrganisationCreationController.ORGANISATION_FORM);

        if (invite.isSuccess()) {
            InviteResource inviteResource = invite.getSuccessObject();
            if (InviteStatusConstants.SEND.equals(inviteResource.getStatus())) {
                // check if there already is a user with this emailaddress
                RestResult existingUserSearch = userService.findUserByEmail(inviteResource.getEmail());
                if (existingUserSearch.isSuccess()) {
                    model.addAttribute("emailAddressRegistered", "true");
                }

                model.addAttribute("invite", inviteResource);
                CookieUtil.saveToCookie(response, INVITE_HASH, hash);
                InviteOrganisationResource inviteOrganisation = inviteRestService.getInviteOrganisationByHash(hash).getSuccessObjectOrThrowException();
                if (inviteOrganisation.getOrganisation() != null) {
                    model.addAttribute("createAccountLink", "/accept-invite/confirm-invited-organisation");
                } else {
                    model.addAttribute("createAccountLink", "/organisation/create/type/new-account-organisation-type");
                }
                return "registration/accept-invite";
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

    @RequestMapping(value = "/accept-invite/confirm-invited-organisation", method = RequestMethod.GET)
    public String confirmInvitedOrganisation(HttpServletResponse response, HttpServletRequest request, Model model) {
        String hash = CookieUtil.getCookieValue(request, INVITE_HASH);
        RestResult<InviteResource> invite = inviteRestService.getInviteByHash(hash);

        if (invite.isSuccess()) {
            InviteResource inviteResource = invite.getSuccessObject();
            if (InviteStatusConstants.SEND.equals(inviteResource.getStatus())) {
                InviteOrganisationResource inviteOrganisation = inviteRestService.getInviteOrganisationByHash(hash).getSuccessObjectOrThrowException();
                OrganisationResource organisation = organisationService.getOrganisationById(inviteOrganisation.getOrganisation());

                model.addAttribute("invite", inviteResource);
                model.addAttribute("inviteOrganisation", inviteOrganisation);
                model.addAttribute("organisation", organisation);
                model.addAttribute("organisationAddress", getOrganisationAddress(organisation));
                model.addAttribute("registerUrl", RegistrationController.BASE_URL + "?" + RegistrationController.ORGANISATION_ID_PARAMETER_NAME + "=" + inviteOrganisation.getOrganisation());
                return "registration/confirm-invited-organisation";
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
