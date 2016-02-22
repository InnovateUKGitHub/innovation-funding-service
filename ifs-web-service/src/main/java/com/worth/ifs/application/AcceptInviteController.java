package com.worth.ifs.application;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.login.LoginForm;
import com.worth.ifs.user.resource.OrganisationTypeResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.OrganisationTypeRestService;
import com.worth.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

// TODO DW - INFUND-1555 - handle rest results
@Controller
public class AcceptInviteController extends AbstractApplicationController {

    public static final String INVITE_HASH = "invite_hash";

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;

    @RequestMapping(value = "/accept-invite/{hash}", method = RequestMethod.GET)
    public String displayContributors(
            @PathVariable("hash") final String hash,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {

        RestResult<InviteResource> invite = inviteRestService.getInviteByHash(hash);


//        model.addAttribute("currentApplication", application);

        if(invite.isSuccess()){
            InviteResource inviteResource = invite.getSuccessObject();
            if(InviteStatusConstants.SEND.equals(inviteResource.getStatus())){
                LoginForm loginForm = new LoginForm();

                // check if there already is a user with this emailaddress
                List<UserResource> existingUsers = userService.findUserByEmail(inviteResource.getEmail()).getSuccessObjectOrThrowException();
                if(existingUsers != null && !existingUsers.isEmpty()){
                    model.addAttribute("emailAddressRegistered", "true");
                }

                model.addAttribute("invite", inviteResource);
                model.addAttribute("loginForm", loginForm);
                CookieUtil.saveToCookie(response, INVITE_HASH, hash);
                return "application-contributors/invite/accept-invite";
            }else{
                cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");
                return "redirect:/login";
            }

        }else {
            cookieFlashMessageFilter.setFlashMessage(response, "inviteNotValid");
            return "redirect:/login";
        }

    }

    // TODO DW - INFUND-1555 - handle rest results
    @RequestMapping(value = "/accept-invite/new-account-organisation-type", method = RequestMethod.GET)
    public String chooseOrganisationType(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Model model
    ){
        String hash = CookieUtil.getCookieValue(request, INVITE_HASH);
        RestResult<InviteResource> invite = inviteRestService.getInviteByHash(hash);

        if(invite.isSuccess() && InviteStatusConstants.SEND.equals(invite.getSuccessObject().getStatus())){
            InviteOrganisationResource inviteOrganisation = inviteRestService.getInviteOrganisationByHash(hash).getSuccessObject();

            List<OrganisationTypeResource> types = organisationTypeRestService.getAll().getSuccessObjectOrThrowException();
            types = types.stream().filter(t -> t.getParentOrganisationType() == null).collect(Collectors.toList());
            model.addAttribute("organisationTypes", types);
            model.addAttribute("inviteOrganisation", inviteOrganisation);
            model.addAttribute("invite", invite.getSuccessObject());
        }else{
            return "redirect:/login";
        }
        return "application-contributors/invite/organisation-type";
    }
}
