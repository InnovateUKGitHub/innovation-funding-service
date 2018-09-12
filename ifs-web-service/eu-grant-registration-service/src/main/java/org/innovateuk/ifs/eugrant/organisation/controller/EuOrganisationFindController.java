package org.innovateuk.ifs.eugrant.organisation.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.organisation.form.EuOrganisationForm;
import org.innovateuk.ifs.eugrant.organisation.form.EuOrganisationTypeForm;
import org.innovateuk.ifs.eugrant.organisation.populator.EuOrganisationFindModelPopulator;
import org.innovateuk.ifs.eugrant.organisation.saver.EuOrganisationSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Provides methods for both:
 * Finding your company or research type organisation through Companies House or JES search.
 * Verifying or amending the address attached to the organisation.
 */
@Controller
@RequestMapping(AbstractEuOrganisationController.BASE_URL + "/" + AbstractEuOrganisationController.FIND_ORGANISATION)
@SecuredBySpring(value = "Controller", description = "TODO", securedType = EuOrganisationFindController.class)
@PreAuthorize("permitAll")
public class EuOrganisationFindController extends AbstractEuOrganisationController {

    @Autowired
    private EuOrganisationSaver organisationSaver;

    @Autowired
    private EuOrganisationFindModelPopulator organisationFindModelPopulator;

    @GetMapping
    public String findOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) EuOrganisationForm organisationForm,
                                   BindingResult bindingResult,
                                   Model model,
                                   HttpServletRequest request) {
        return getOrganisationType(type -> {
            model.addAttribute("model", organisationFindModelPopulator.populate(type, organisationForm, request));
            return TEMPLATE_PATH + "/" + FIND_ORGANISATION;
        });
    }

    @PostMapping(params = "organisationSearching")
    public String searchOrganisation(@Valid @ModelAttribute(name = ORGANISATION_FORM) EuOrganisationForm organisationForm,
                                     BindingResult bindingResult,
                                     Model model,
                                     HttpServletRequest request) {
        return findOrganisation(organisationForm, bindingResult, model, request);
    }

    @PostMapping
    public String saveOrganisation(@Valid @ModelAttribute(name = ORGANISATION_FORM) EuOrganisationForm organisationForm,
                                   BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   Model model,
                                   HttpServletRequest request) {
        Supplier<String> failureView =  () -> findOrganisation(organisationForm, bindingResult, model, request);
        return validationHandler.failNowOrSucceedWith(failureView,
                () -> getOrganisationType(type ->
                    validationHandler.addAnyErrors(
                            organisationSaver.save(organisationForm, type))
                    .failNowOrSucceedWith(failureView,
                            () -> "redirect:/organisation/view")
                ));
    }

    private String getOrganisationType(Function<EuOrganisationType, String> success) {
        return organisationCookieService.getOrganisationTypeCookieValue()
                .map(EuOrganisationTypeForm::getOrganisationType)
                .map(success)
                .orElse("redirect:/organisation/type");
    }

}
