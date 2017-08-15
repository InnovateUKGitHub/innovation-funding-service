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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/type")
@PreAuthorize("permitAll")
public class OrganisationCreationContributorTypeController extends AbstractOrganisationCreationController {
    private static final Log LOG = LogFactory.getLog(OrganisationCreationContributorTypeController.class);

    private static final String ORGANISATION_TYPE = "organisationType";

    @Autowired
    private InviteRestService inviteRestService;

    @GetMapping("/new-account-organisation-type")
    public String chooseOrganisationType(HttpServletRequest request,
                                         Model model,
                                         @ModelAttribute(name = "form", binding = false) OrganisationTypeForm form,
                                         BindingResult bindingResult,
                                         HttpServletResponse response,
                                         @RequestParam(value = ORGANISATION_TYPE, required = false) Long organisationTypeId,
                                         @RequestParam(value = "invalid", required = false) String invalid) {
        String hash = registrationCookieService.getInviteHashCookieValue(request).get();
        registrationCookieService.deleteOrganisationCreationCookie(response);
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
        return TEMPLATE_PATH + "/organisation-type";
    }

    @PostMapping("/new-account-organisation-type")
    public String chooseOrganisationType(HttpServletResponse response,
                                         @ModelAttribute @Valid OrganisationTypeForm organisationTypeForm,
                                         BindingResult bindingResult) {
        registrationCookieService.deleteOrganisationCreationCookie(response);
        if (bindingResult.hasErrors()) {
            LOG.debug("redirect because validation errors");
            return "redirect:/organisation/create/new-account-organisation-type?invalid";
        } else {
            registrationCookieService.saveToOrganisationTypeCookie(organisationTypeForm, response);
            LOG.debug("redirect for organisation creation");
            return "redirect:" + BASE_URL + "/" + FIND_ORGANISATION;
        }
    }
}