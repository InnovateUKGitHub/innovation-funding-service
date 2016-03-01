package com.worth.ifs.application;

import com.worth.ifs.application.form.OrganisationTypeForm;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.login.LoginForm;
import com.worth.ifs.organisation.OrganisationCreationController;
import com.worth.ifs.user.domain.OrganisationTypeEnum;
import com.worth.ifs.user.resource.OrganisationTypeResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.OrganisationTypeRestService;
import com.worth.ifs.util.CookieUtil;
import com.worth.ifs.util.JsonUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is use as an entry point to accept a invite, to a application.
 */
@Controller
public class AcceptInviteController extends AbstractApplicationController {
    public static final String INVITE_HASH = "invite_hash";
    public static final String ORGANISATION_TYPE = "organisationType";
    private final Log log = LogFactory.getLog(getClass());
    Validator validator;
    @Autowired
    private InviteRestService inviteRestService;
    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @RequestMapping(value = "/accept-invite/{hash}", method = RequestMethod.GET)
    public String displayContributors(
            @PathVariable("hash") final String hash,
            HttpServletResponse response,
            Model model) {
        RestResult<InviteResource> invite = inviteRestService.getInviteByHash(hash);
        CookieUtil.saveToCookie(response, INVITE_HASH, "");
        CookieUtil.removeCookie(response, OrganisationCreationController.ORGANISATION_FORM);

        if (invite.isSuccess()) {
            InviteResource inviteResource = invite.getSuccessObject();
            if (InviteStatusConstants.SEND.equals(inviteResource.getStatus())) {
                LoginForm loginForm = new LoginForm();

                // check if there already is a user with this emailaddress
                List<UserResource> existingUsers = userService.findUserByEmail(inviteResource.getEmail()).getSuccessObjectOrThrowException();
                if (existingUsers != null && !existingUsers.isEmpty()) {
                    model.addAttribute("emailAddressRegistered", "true");
                }

                model.addAttribute("invite", inviteResource);
                model.addAttribute("loginForm", loginForm);
                CookieUtil.saveToCookie(response, INVITE_HASH, hash);
                return "application-contributors/invite/accept-invite";
            } else {
                cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");
                return "redirect:/login";
            }

        } else {
            cookieFlashMessageFilter.setFlashMessage(response, "inviteNotValid");
            return "redirect:/login";
        }
    }

    @RequestMapping(value = "/accept-invite/new-account-organisation-type", method = RequestMethod.GET)
    public String chooseOrganisationType(HttpServletRequest request,
                                         Model model,
                                         @ModelAttribute OrganisationTypeForm organisationTypeForm,
                                         BindingResult bindingResult,
                                         HttpServletResponse response,
                                         @RequestParam(value = ORGANISATION_TYPE, required = false) Long organisationTypeId,
                                         @RequestParam(value = "invalid", required = false) String invalid

    ) {
        String hash = CookieUtil.getCookieValue(request, INVITE_HASH);
        CookieUtil.removeCookie(response, OrganisationCreationController.ORGANISATION_FORM);
        RestResult<InviteResource> invite = inviteRestService.getInviteByHash(hash);

        if (invalid != null) {
            validator.validate(organisationTypeForm, bindingResult);
        }

        if (invite.isSuccess() && InviteStatusConstants.SEND.equals(invite.getSuccessObject().getStatus())) {
            InviteOrganisationResource inviteOrganisation = inviteRestService.getInviteOrganisationByHash(hash).getSuccessObject();

            List<OrganisationTypeResource> types = organisationTypeRestService.getAll().getSuccessObjectOrThrowException();
            if (organisationTypeId == null) {
                types = types.stream()
                        .filter(t -> t.getParentOrganisationType() == null)
                        .collect(Collectors.toList());
            } else {
                model.addAttribute("organisationParentType", OrganisationTypeEnum.getFromId(organisationTypeId));
                organisationTypeRestService.findOne(organisationTypeId).andOnSuccessReturn(r -> {
                    model.addAttribute("organisationParentTypeTitle", r.getName());
                    return r;
                });
                types = types.stream()
                        .filter(t -> t.getParentOrganisationType() != null)
                        .filter(t -> t.getParentOrganisationType().equals(organisationTypeId))
                        .collect(Collectors.toList());
            }
            model.addAttribute("organisationTypeForm", organisationTypeForm);
            model.addAttribute("organisationTypes", types);
            model.addAttribute("inviteOrganisation", inviteOrganisation);
            model.addAttribute("invite", invite.getSuccessObject());
        } else {
            return "redirect:/login";
        }
        return "application-contributors/invite/organisation-type";
    }

    @RequestMapping(value = "/accept-invite/new-account-organisation-type", method = RequestMethod.POST)
    public String chooseOrganisationType(HttpServletResponse response,
                                         @ModelAttribute @Valid OrganisationTypeForm organisationTypeForm,
                                         BindingResult bindingResult

    ) {
        CookieUtil.removeCookie(response, OrganisationCreationController.ORGANISATION_FORM);
        Long organisationTypeId = organisationTypeForm.getOrganisationType();
        if (bindingResult.hasErrors()) {
            log.debug("redirect because validation errors");
            return "redirect:/accept-invite/new-account-organisation-type?invalid";
        } else if (OrganisationTypeEnum.getFromId(organisationTypeId).hasChildren()) {
            String orgTypeForm = JsonUtil.getSerializedObject(organisationTypeForm);
            CookieUtil.saveToCookie(response, ORGANISATION_TYPE, orgTypeForm);
            log.debug("redirect for organisation subtype");
            return "redirect:/accept-invite/new-account-organisation-type/?" + ORGANISATION_TYPE + '=' + organisationTypeForm.getOrganisationType();
        } else {
            String orgTypeForm = JsonUtil.getSerializedObject(organisationTypeForm);
            CookieUtil.saveToCookie(response, ORGANISATION_TYPE, orgTypeForm);
            log.debug("redirect for organisation creation");
            return "redirect:/organisation/create/find-organisation";
        }
    }
}
