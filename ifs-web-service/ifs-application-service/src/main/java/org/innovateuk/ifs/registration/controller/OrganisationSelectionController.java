package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.registration.form.OrganisationSelectionForm;
import org.innovateuk.ifs.registration.populator.OrganisationSelectionViewModelPopulator;
import org.innovateuk.ifs.registration.service.OrganisationJourneyEnd;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
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
import java.util.function.Supplier;

import static org.innovateuk.ifs.registration.controller.AbstractOrganisationCreationController.BASE_URL;
import static org.innovateuk.ifs.registration.controller.AbstractOrganisationCreationController.LEAD_ORGANISATION_TYPE;
import static org.innovateuk.ifs.registration.controller.OrganisationCreationLeadTypeController.NOT_ELIGIBLE;

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
    private OrganisationRestService organisationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

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
                request,
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
        Supplier<String> failureView = () -> viewPreviousOrganisations(request, form, bindingResult, user, model);
        return validationHandler.failNowOrSucceedWith(failureView, validateEligibility(request, response, user, form));

    }

    private boolean cannotSelectOrganisation(UserResource user) {
        return user == null || !user.hasRole(Role.APPLICANT);
    }

    private String nextPageInFlow(HttpServletRequest request) {
        if (registrationCookieService.isCollaboratorJourney(request)) {
            return "/organisation/create/contributor-organisation-type";
        } else {
            return "/organisation/create/initialize";
        }
    }

    private Supplier<String> validateEligibility(HttpServletRequest request, HttpServletResponse response, UserResource user, OrganisationSelectionForm form) {
        return () -> {
            if (registrationCookieService.isLeadJourney(request)) {
                CompetitionResource competition = competitionRestService.getCompetitionById(registrationCookieService.getCompetitionIdCookieValue(request).get()).getSuccess();
                OrganisationResource organisation = organisationRestService.getOrganisationById(form.getSelectedOrganisationId()).getSuccess();
                if (!competition.getLeadApplicantTypes().contains(organisation.getOrganisationType())) {
                    return "redirect:" + BASE_URL + "/" + LEAD_ORGANISATION_TYPE + "/" + NOT_ELIGIBLE;
                }
            }
            return organisationJourneyEnd.completeProcess(request, response, user, form.getSelectedOrganisationId());
        };
    }
}
