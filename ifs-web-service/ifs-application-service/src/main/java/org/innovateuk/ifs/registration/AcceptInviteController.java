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
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.invite.service.InviteService;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.registration.model.AcceptRejectApplicationInviteModelPopulator;
import org.innovateuk.ifs.registration.service.RegistrationService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.service.InviteServiceImpl.INVITE_ALREADY_ACCEPTED;
import static org.innovateuk.ifs.invite.service.InviteServiceImpl.INVITE_HASH;
import static org.innovateuk.ifs.registration.OrganisationCreationController.ORGANISATION_FORM;


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

    @Autowired
    private AcceptRejectApplicationInviteModelPopulator acceptRejectApplicationInviteModelPopulator;

    private static final String ALREADY_ACCEPTED_VIEW = "redirect:/login";
    private static final String LOGGED_IN_WITH_ANOTHER_USER_VIEW = "registration/logged-in-with-another-user-failure";
    private static final String ACCEPT_INVITE_NEW_USER_VIEW = "registration/accept-invite-new-user";
    private static final String ACCEPT_INVITE_EXISTING_USER_VIEW = "registration/accept-invite-existing-user";
    private static final String SUCCESS_VIEW = "registration/accept-invite-new-user";

    // return "redirect:/accept-invite-authenticated/confirm-invited-organisation"; // qqRP TODO remove

    @RequestMapping(value = "/accept-invite/{hash}", method = RequestMethod.GET)
    public String inviteEntryPage(
            @PathVariable("hash") final String hash,
            @ModelAttribute("loggedInUser") UserResource loggedInUser,
            HttpServletResponse response,
            Model model) {
        clearDownInviteFlowCookies(response); // This is the initial entry point. Clear any previous state from cookies.
        return inviteRestService.getInviteByHash(hash).andOnSuccess(invite -> {
                    if (!SENT.equals(invite.getStatus())) {
                        return restSuccess(alreadyAcceptedView(response));
                    }
                    return inviteRestService.getInviteOrganisationByHash(hash).andOnSuccessReturn(inviteOrganisation -> {
                                if (loggedInAsNonInviteUser(invite, loggedInUser)){
                                    return LOGGED_IN_WITH_ANOTHER_USER_VIEW;
                                }
                                // Success
                                addInviteHashCookie(response, invite.getHash()); // Add the hash to a cookie for later flow lookup.
                                model.addAttribute("model", acceptRejectApplicationInviteModelPopulator.populateModel(invite, inviteOrganisation));
                                return invite.getUser() == null ? ACCEPT_INVITE_NEW_USER_VIEW : ACCEPT_INVITE_EXISTING_USER_VIEW;
                            }
                    );
                }
        ).getSuccessObject();
    }

    private void clearDownInviteFlowCookies(HttpServletResponse response) {
        cookieUtil.removeCookie(response, ORGANISATION_FORM);
        cookieUtil.removeCookie(response, INVITE_HASH);
    }

    private void addInviteHashCookie(HttpServletResponse response, String hash) {
        cookieUtil.saveToCookie(response, INVITE_HASH, hash);
    }

    private String alreadyAcceptedView(HttpServletResponse response) {
        cookieFlashMessageFilter.setFlashMessage(response, INVITE_ALREADY_ACCEPTED);
        return ALREADY_ACCEPTED_VIEW;
    }

    private boolean loggedInAsNonInviteUser(ApplicationInviteResource invite, UserResource loggedInUser) {
        if (loggedInUser == null){
            return false;
        } else if (invite.getEmail().equalsIgnoreCase(loggedInUser.getEmail())){
            return false;
        }
        return true;
    }


//    private void addCreateAccountURL(Model model, InviteOrganisationResource inviteOrganisation) {
//        if (inviteOrganisation.getOrganisation() != null) {
//            model.addAttribute("createAccountLink", "/accept-invite/confirm-invited-organisation");
//        } else {
//            model.addAttribute("createAccountLink", "/organisation/create/type/new-account-organisation-type");
//        }
//    }

    @RequestMapping(value = "/accept-invite/confirm-invited-organisation", method = RequestMethod.GET)
    public String confirmInvitedOrganisation(HttpServletResponse response, HttpServletRequest request, Model model) {

        ApplicationInviteResource inviteResource = inviteService.getInviteByRequest(request, response);
        if (SENT.equals(inviteResource.getStatus())) {
            InviteOrganisationResource inviteOrganisation = inviteRestService.getInviteOrganisationByHash(inviteResource.getHash()).getSuccessObjectOrThrowException();
            OrganisationResource organisation = organisationService.getOrganisationByIdForAnonymousUserFlow(inviteOrganisation.getOrganisation());

            cookieUtil.saveToCookie(response, RegistrationController.ORGANISATION_ID_PARAMETER_NAME, String.valueOf(inviteOrganisation.getOrganisation()));

            model.addAttribute("invite", inviteResource);
            model.addAttribute("organisation", organisation);
            model.addAttribute("organisationAddress", getOrganisationAddress(organisation));
            model.addAttribute("registerUrl", RegistrationController.BASE_URL);
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
