package org.innovateuk.ifs.registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.BaseController;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.invite.service.InviteService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.invite.service.InviteServiceImpl.INVITE_HASH;


/**
 * This class is use as an entry point to accept a invite, to a application.
 */
@Controller
@PreAuthorize("permitAll")
public class AcceptInviteController extends BaseController {
    private static final Log LOG = LogFactory.getLog(AcceptInviteController.class);

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private InviteService inviteService;
    @Autowired
    private InviteRestService inviteRestService;

    @RequestMapping(value = "/accept-invite/{hash}", method = RequestMethod.GET)
    public String inviteEntryPage(
            @PathVariable("hash") final String hash,
            HttpServletResponse response,
            HttpServletRequest request,
            Model model) {
        cookieUtil.removeCookie(response, OrganisationCreationController.ORGANISATION_FORM);

        ApplicationInviteResource inviteResource = inviteService.getInviteByHash(hash, response);
        if (!InviteStatus.SENT.equals(inviteResource.getStatus())) {
            return "redirect:/login";
        } else {
            cookieUtil.saveToCookie(response, INVITE_HASH, hash);
            InviteOrganisationResource inviteOrganisation = inviteRestService.getInviteOrganisationByHash(hash).getSuccessObjectOrThrowException();

            // check if there already is a user with this emailaddress
            RestResult<Boolean> existingUserSearch = inviteRestService.checkExistingUser(hash);
            // User already registered?
            String redirectUrl = handleExistingUser(hash, response, request, model, inviteResource, existingUserSearch, inviteOrganisation);
            if (redirectUrl != null) return redirectUrl;

            model.addAttribute("invite", inviteResource);
            addCreateAccountURL(model, inviteOrganisation);
            return "registration/accept-invite";
        }
    }

    private String handleExistingUser(@PathVariable("hash") String hash, HttpServletResponse response, HttpServletRequest request, Model model, ApplicationInviteResource inviteResource, RestResult<Boolean> existingUserSearch, InviteOrganisationResource inviteOrganisation) {
        if (existingUserSearch.getSuccessObject()) {

            model.addAttribute("emailAddressRegistered", "true");

            UserResource loggedInUser = userAuthenticationService.getAuthenticatedUser(request);
            if (loggedInUser != null) {
                Map<String, String> failureMessages = registrationService.getInvalidInviteMessages(loggedInUser, inviteResource, inviteOrganisation);

                if (failureMessages.size() > 0){
                    failureMessages.forEach((messageKey, messageValue) -> model.addAttribute(messageKey, messageValue));

                    return "registration/accept-invite-failure";
                }else{
                    cookieUtil.saveToCookie(response, INVITE_HASH, hash);
                    return "redirect:/accept-invite-authenticated/confirm-invited-organisation";
                }
            }else{
                cookieUtil.saveToCookie(response, INVITE_HASH, hash);
                // just show the login link
            }
        }else{
            LOG.debug("Not found a user with hash "+ hash);
        }
        return null;
    }

    private void addCreateAccountURL(Model model, InviteOrganisationResource inviteOrganisation) {
        if (inviteOrganisation.getOrganisation() != null) {
            model.addAttribute("createAccountLink", "/accept-invite/confirm-invited-organisation");
        } else {
            model.addAttribute("createAccountLink", "/organisation/create/type/new-account-organisation-type");
        }
    }

    @RequestMapping(value = "/accept-invite/confirm-invited-organisation", method = RequestMethod.GET)
    public String confirmInvitedOrganisation(HttpServletResponse response, HttpServletRequest request, Model model) {

        ApplicationInviteResource inviteResource = inviteService.getInviteByRequest(request, response);
        if (InviteStatus.SENT.equals(inviteResource.getStatus())) {
            InviteOrganisationResource inviteOrganisation = inviteRestService.getInviteOrganisationByHash(inviteResource.getHash()).getSuccessObjectOrThrowException();
            OrganisationResource organisation = organisationService.getOrganisationByIdForAnonymousUserFlow(inviteOrganisation.getOrganisation());

            cookieUtil.saveToCookie(response, RegistrationController.ORGANISATION_ID_PARAMETER_NAME, String.valueOf(inviteOrganisation.getOrganisation()));

            model.addAttribute("model",
                    new ConfirmOrganisationInviteOrganisation(inviteResource, organisation,
                            getOrganisationAddress(organisation), RegistrationController.BASE_URL));

            return "registration/confirm-invited-organisation";
        } else {
            return "redirect:/login";
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
