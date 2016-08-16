package com.worth.ifs.registration;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.registration.form.OrganisationTypeForm;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import com.worth.ifs.user.resource.OrganisationTypeResource;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/organisation/create/type/")
public class OrganisationTypeCreationController {
    private static final Log LOG = LogFactory.getLog(OrganisationTypeCreationController.class);
    Validator validator;
    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }
    @Autowired
    private InviteRestService inviteRestService;
    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;
    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "/new-account-organisation-type", method = RequestMethod.GET)
    public String chooseOrganisationType(HttpServletRequest request,
                                         Model model,
                                         @ModelAttribute OrganisationTypeForm organisationTypeForm,
                                         BindingResult bindingResult,
                                         HttpServletResponse response,
                                         @RequestParam(value = AcceptInviteController.ORGANISATION_TYPE, required = false) Long organisationTypeId,
                                         @RequestParam(value = "invalid", required = false) String invalid

    ) {
        String hash = CookieUtil.getCookieValue(request, AcceptInviteController.INVITE_HASH);
        CookieUtil.removeCookie(response, OrganisationCreationController.ORGANISATION_FORM);
        RestResult<ApplicationInviteResource> invite = inviteRestService.getInviteByHash(hash);

        if (invalid != null) {
            validator.validate(organisationTypeForm, bindingResult);
        }

        if (invite.isSuccess() && InviteStatus.SENT.equals(invite.getSuccessObject().getStatus())) {
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
        return "registration/organisation/organisation-type";
    }

    @RequestMapping(value = "/new-account-organisation-type", method = RequestMethod.POST)
    public String chooseOrganisationType(HttpServletResponse response,
                                         @ModelAttribute @Valid OrganisationTypeForm organisationTypeForm,
                                         BindingResult bindingResult
    ) {
        CookieUtil.removeCookie(response, OrganisationCreationController.ORGANISATION_FORM);
        Long organisationTypeId = organisationTypeForm.getOrganisationType();
        if (bindingResult.hasErrors()) {
            LOG.debug("redirect because validation errors");
            return "redirect:/organisation/create/type/new-account-organisation-type?invalid";
        } else if (OrganisationTypeEnum.getFromId(organisationTypeId).hasChildren()) {
            String orgTypeForm = JsonUtil.getSerializedObject(organisationTypeForm);
            CookieUtil.saveToCookie(response, AcceptInviteController.ORGANISATION_TYPE, orgTypeForm);
            LOG.debug("redirect for organisation subtype");
            return "redirect:/organisation/create/type/new-account-organisation-type/?" + AcceptInviteController.ORGANISATION_TYPE + '=' + organisationTypeForm.getOrganisationType();
        } else {
            String orgTypeForm = JsonUtil.getSerializedObject(organisationTypeForm);
            CookieUtil.saveToCookie(response, AcceptInviteController.ORGANISATION_TYPE, orgTypeForm);
            LOG.debug("redirect for organisation creation");
            return "redirect:/organisation/create/find-organisation";
        }
    }
}