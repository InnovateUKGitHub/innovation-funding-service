package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.registration.form.OrganisationSelectionForm;
import org.innovateuk.ifs.registration.populator.OrganisationSelectionViewModelPopulator;
import org.innovateuk.ifs.registration.service.OrganisationJourneyEnd;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.registration.validator.OrganisationSelectionFormValidator;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

@RequestMapping("/organisation/select")
@SecuredBySpring(value="Controller", description = "TODO", securedType = OrganisationSelectionController.class)
@PreAuthorize("hasAnyAuthority('applicant', 'assessor')")
@Controller
public class OrganisationSelectionController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private RegistrationCookieService registrationCookieService;

    @Autowired
    private OrganisationSelectionViewModelPopulator organisationSelectionViewModelPopulator;

    @Autowired
    private OrganisationJourneyEnd organisationJourneyEnd;

    @Autowired
    private OrganisationSelectionFormValidator validator;

    @GetMapping
    public String viewPreviousOrganisations(HttpServletRequest request,
                                            @ModelAttribute(FORM_ATTR_NAME) OrganisationSelectionForm form,
                                            BindingResult bindingResult,
                                            UserResource user,
                                            Model model) {
        if (cannotSelectOrganisation(user)) {
            return "redirect:" + nextPageInFlow(request);
        }
        model.addAttribute("model", organisationSelectionViewModelPopulator.populate(user,
                registrationCookieService.getCompetitionIdCookieValue(request),
                registrationCookieService.getInviteHashCookieValue(request),
                nextPageInFlow(request)));
        return "registration/organisation/select-organisation";
    }

    @PostMapping
    public String selectOrganisation(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @ModelAttribute(FORM_ATTR_NAME) @Valid OrganisationSelectionForm form,
                                     BindingResult bindingResult,
                                     ValidationHandler validationHandler,
                                     UserResource user,
                                     Model model) {
        validator.validate(form, bindingResult);
        Supplier<String> failureView = () -> viewPreviousOrganisations(request, form, bindingResult, user, model);
        return validationHandler.failNowOrSucceedWith(failureView, () ->
                organisationJourneyEnd.completeProcess(request, response, user, form.getSelectedOrganisationId()));
    }

    private boolean cannotSelectOrganisation(UserResource user) {
        return user == null || !user.hasRole(Role.APPLICANT);
    }

    private String nextPageInFlow(HttpServletRequest request) {
        Optional<String> invite = registrationCookieService.getInviteHashCookieValue(request);
        if (invite.isPresent()) {
            return "/organisation/create/contributor-organisation-type";
        } else {
            return "/organisation/create/initialize";
        }
    }



}
