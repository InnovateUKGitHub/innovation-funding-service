package org.innovateuk.ifs.registration.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.viewmodel.ContributorOrganisationTypeViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
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
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/contributor-organisation-type")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = OrganisationCreationContributorTypeController.class)
@PreAuthorize("permitAll")
public class OrganisationCreationContributorTypeController extends AbstractOrganisationCreationController {
    private static final Log LOG = LogFactory.getLog(OrganisationCreationContributorTypeController.class);

    private static final String ORGANISATION_TYPE = "organisationType";

    @Autowired
    private InviteRestService inviteRestService;

    @GetMapping
    public String chooseOrganisationType(HttpServletRequest request,
                                         Model model,
                                         @ModelAttribute(name = "form", binding = false) OrganisationTypeForm form,
                                         BindingResult bindingResult,
                                         HttpServletResponse response,
                                         @RequestParam(value = ORGANISATION_TYPE, required = false) Long organisationTypeId,
                                         @RequestParam(value = "invalid", required = false) String invalid,
                                         UserResource user) {
        registrationCookieService.deleteOrganisationCreationCookie(response);

        if (invalid != null) {
            validator.validate(form, bindingResult);
        }

        List<OrganisationTypeResource> types = organisationTypeRestService.getAll().getSuccess();
        types = types.stream()
                    .filter(t -> t.getParentOrganisationType() == null)
                    .collect(Collectors.toList());
        model.addAttribute("form", form);
        model.addAttribute("model", new ContributorOrganisationTypeViewModel(types));
        addPageSubtitleToModel(request, user, model);
        return TEMPLATE_PATH + "/contributor-organisation-type";
    }

    @PostMapping
    public String chooseOrganisationType(HttpServletResponse response,
                                         @ModelAttribute @Valid OrganisationTypeForm organisationTypeForm,
                                         BindingResult bindingResult) {
        registrationCookieService.deleteOrganisationCreationCookie(response);
        if (bindingResult.hasErrors()) {
            LOG.debug("redirect because validation errors");
            return "redirect:/organisation/create/contributor-organisation-type?invalid";
        } else {
            registrationCookieService.saveToOrganisationTypeCookie(organisationTypeForm, response);
            LOG.debug("redirect for organisation creation");
            return "redirect:" + BASE_URL + "/" + FIND_ORGANISATION;
        }
    }
}