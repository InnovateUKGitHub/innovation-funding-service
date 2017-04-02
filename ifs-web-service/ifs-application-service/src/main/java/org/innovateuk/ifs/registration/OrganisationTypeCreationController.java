package org.innovateuk.ifs.registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.viewmodel.OrganisationCreationViewModel;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
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

import static org.innovateuk.ifs.registration.AbstractAcceptInviteController.INVITE_HASH;
import static org.innovateuk.ifs.registration.AbstractAcceptInviteController.ORGANISATION_TYPE;

@Controller
@RequestMapping("/organisation/create/type/")
@PreAuthorize("permitAll")
public class OrganisationTypeCreationController {
    private static final Log LOG = LogFactory.getLog(OrganisationTypeCreationController.class);
    Validator validator;
    @Autowired
    @Qualifier("mvcValidator")
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CookieUtil cookieUtil;

    @GetMapping("/new-account-organisation-type")
    public String chooseOrganisationType(HttpServletRequest request,
                                         Model model,
                                         @ModelAttribute(name = "form") OrganisationTypeForm form,
                                         BindingResult bindingResult,
                                         HttpServletResponse response,
                                         @RequestParam(value = ORGANISATION_TYPE, required = false) Long organisationTypeId,
                                         @RequestParam(value = "invalid", required = false) String invalid) {
        String hash = cookieUtil.getCookieValue(request, INVITE_HASH);
        cookieUtil.removeCookie(response, OrganisationCreationController.ORGANISATION_FORM);
        RestResult<ApplicationInviteResource> invite = inviteRestService.getInviteByHash(hash);

        if (invalid != null) {
            validator.validate(form, bindingResult);
        }

        if (invite.isSuccess() && InviteStatus.SENT.equals(invite.getSuccessObject().getStatus())) {
            List<OrganisationTypeResource> types = organisationTypeRestService.getAll().getSuccessObjectOrThrowException();
            types = types.stream()
                        .filter(t -> t.getParentOrganisationType() == null)
                        .collect(Collectors.toList());
            model.addAttribute("form", form);
            model.addAttribute("model", new OrganisationCreationViewModel(types, invite.getSuccessObject()));
        } else {
            return "redirect:/login";
        }
        return "registration/organisation/organisation-type";
    }

    @PostMapping("/new-account-organisation-type")
    public String chooseOrganisationType(HttpServletResponse response,
                                         @ModelAttribute @Valid OrganisationTypeForm organisationTypeForm,
                                         BindingResult bindingResult) {
        cookieUtil.removeCookie(response, OrganisationCreationController.ORGANISATION_FORM);
        if (bindingResult.hasErrors()) {
            LOG.debug("redirect because validation errors");
            return "redirect:/organisation/create/type/new-account-organisation-type?invalid";
        } else {
            String orgTypeForm = JsonUtil.getSerializedObject(organisationTypeForm);
            cookieUtil.saveToCookie(response, ORGANISATION_TYPE, orgTypeForm);
            LOG.debug("redirect for organisation creation");
            return "redirect:/organisation/create/find-organisation";
        }
    }
}