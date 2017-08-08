package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;


public class AbstractAcceptInviteController {

    private static final String ALREADY_ACCEPTED_VIEW = "redirect:/login";

    public static final String INVITE_ALREADY_ACCEPTED = "inviteAlreadyAccepted";

    @Autowired
    protected CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    protected RegistrationCookieService registrationCookieService;

    protected static final String LOGGED_IN_WITH_ANOTHER_USER_VIEW = "registration/logged-in-with-another-user-failure";

    protected final Runnable clearDownInviteFlowCookiesFn(HttpServletResponse response) {
        return () -> clearDownInviteFlowCookies(response);
    }

    protected final void clearDownInviteFlowCookies(HttpServletResponse response) {
        registrationCookieService.deleteOrganisationCreationCookie(response);
        registrationCookieService.deleteOrganisationIdCookie(response);
        registrationCookieService.deleteInviteHashCookie(response);
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
