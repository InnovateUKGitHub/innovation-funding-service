package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.BaseController;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.innovateuk.ifs.registration.OrganisationCreationController.ORGANISATION_FORM;
import static org.innovateuk.ifs.registration.RegistrationController.ORGANISATION_ID_PARAMETER_NAME;


public class AbstractAcceptInviteController extends BaseController {

    private static final String ALREADY_ACCEPTED_VIEW = "redirect:/login";
    public static final String ORGANISATION_TYPE = "organisationType";
    public static final String INVITE_ALREADY_ACCEPTED = "inviteAlreadyAccepted";
    public static final String INVITE_HASH = "invite_hash";

    @Autowired
    protected CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    protected CookieUtil cookieUtil;


    protected static final String LOGGED_IN_WITH_ANOTHER_USER_VIEW = "registration/logged-in-with-another-user-failure";

    protected final String getInviteHashCookie(HttpServletRequest request){
        return cookieUtil.getCookieValue(request, INVITE_HASH);
    }

    protected final Runnable clearDownInviteFlowCookiesFn(HttpServletResponse response) {
        return () -> clearDownInviteFlowCookies(response);
    }

    protected final void clearDownInviteFlowCookies(HttpServletResponse response) {
        cookieUtil.removeCookie(response, ORGANISATION_FORM);
        cookieUtil.removeCookie(response, INVITE_HASH);
        cookieUtil.removeCookie(response, ORGANISATION_ID_PARAMETER_NAME);
    }

    protected final void putInviteHashCookie(HttpServletResponse response, String hash) {
        cookieUtil.saveToCookie(response, INVITE_HASH, hash);
    }

    protected final String alreadyAcceptedView(HttpServletResponse response) {
        clearDownInviteFlowCookies(response);
        cookieFlashMessageFilter.setFlashMessage(response, INVITE_ALREADY_ACCEPTED);
        return ALREADY_ACCEPTED_VIEW;
    }

    protected final boolean loggedInAsNonInviteUser(ApplicationInviteResource invite, UserResource loggedInUser) {
        if (loggedInUser == null){
            return false;
        } else if (invite.getEmail().equalsIgnoreCase(loggedInUser.getEmail())){
            return false;
        }
        return true;
    }

    /**
     * Get the most import address of the organisation. If there is a operating address, use that otherwise just get the first one.
     */
    protected final AddressResource getOrganisationAddress(OrganisationResource organisation) {
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
