package com.worth.ifs.application;

import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.login.LoginForm;
import com.worth.ifs.service.CookieService;
import com.worth.ifs.user.resource.OrganisationTypeResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.OrganisationTypeRestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AcceptInviteController extends AbstractApplicationController {
    public static final String INVITE_HASH = "invite_hash";
    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    private CookieService cookieService;
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
        Optional<InviteResource> invite = inviteRestService.getInviteByHash(hash);


//        model.addAttribute("currentApplication", application);

        if(invite.isPresent()){
            InviteResource inviteResource = invite.get();
            if(InviteStatusConstants.SEND.equals(inviteResource.getStatus())){
                LoginForm loginForm = new LoginForm();

                // check if there already is a user with this emailaddress
                List<UserResource> existingUsers = userService.findUserByEmail(inviteResource.getEmail());
                if(!existingUsers.isEmpty()){
                    model.addAttribute("emailAddressRegistered", "true");
                }

                model.addAttribute("invite", invite.get());
                model.addAttribute("loginForm", loginForm);
                cookieService.saveToCookie(response, INVITE_HASH, hash);
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
    @RequestMapping(value = "/accept-invite/new-account-organisation-type", method = RequestMethod.GET)
    public String chooseOrganisationType(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Model model
    ){
        String hash = cookieService.getCookieValue(request, INVITE_HASH);
        Optional<InviteResource> invite = inviteRestService.getInviteByHash(hash);

        if(invite.isPresent() && InviteStatusConstants.SEND.equals(invite.get().getStatus())){
            Optional<InviteOrganisationResource> inviteOrganisation = inviteRestService.getInviteOrganisationByHash(hash);

            List<OrganisationTypeResource> types = organisationTypeRestService.getAll();
            types = types.stream().filter(t -> t.getParentOrganisationType() == null).collect(Collectors.toList());
            model.addAttribute("organisationTypes", types);
            model.addAttribute("inviteOrganisation", inviteOrganisation.get());
            model.addAttribute("invite", invite.get());
        }else{
            return "redirect:/login";
        }
        return "application-contributors/invite/organisation-type";
    }
}
