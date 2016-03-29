package com.worth.ifs.application;

import com.worth.ifs.user.service.UserService;
import com.worth.ifs.commons.error.exception.InvalidURLException;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.registration.OrganisationCreationController;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.user.service.OrganisationTypeRestService;
import com.worth.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

/**
 * This class is use as an entry point to accept a invite, to a application.
 */
@Controller
public class AcceptInviteController {
    public static final String INVITE_HASH = "invite_hash";
    public static final String ORGANISATION_TYPE = "organisationType";
    Validator validator;
    @Autowired
    private InviteRestService inviteRestService;
    @Autowired
    private UserService userService;
    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;

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
                if (existingUserSearch.isSuccess()){
                    model.addAttribute("emailAddressRegistered", "true");
                }

                model.addAttribute("invite", inviteResource);
                CookieUtil.saveToCookie(response, INVITE_HASH, hash);
                return "registration/accept-invite";
            } else {
                CookieUtil.removeCookie(response, INVITE_HASH);
                cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");
                return "redirect:/login";
            }
        }else{
            CookieUtil.removeCookie(response, INVITE_HASH);
            throw new InvalidURLException("Invite url is not valid", null);
        }
    }

}
