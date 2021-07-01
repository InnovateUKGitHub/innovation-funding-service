package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.populator.OrganisationSelectionViewModelPopulator;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.viewmodel.OrganisationSelectionViewModel;
import org.innovateuk.ifs.registration.form.OrganisationSelectionForm;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import static org.innovateuk.ifs.organisation.controller.OrganisationCreationTypeController.NOT_ELIGIBLE;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.isValidCollaborator;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.isValidKtpCollaborator;

@RequestMapping("/organisation/select")
@Controller
public class OrganisationSelectionController extends AbstractOrganisationCreationController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private OrganisationSelectionViewModelPopulator organisationSelectionViewModelPopulator;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Value("${ifs.new.organisation.search.enabled:false}")
    private Boolean newOrganisationSearchEnabled;

    @PreAuthorize("hasPermission(#user,'APPLICATION_CREATION')")
    @GetMapping
    public String viewPreviousOrganisations(HttpServletRequest request,
                                            @ModelAttribute(FORM_ATTR_NAME) OrganisationSelectionForm form,
                                            BindingResult bindingResult,
                                            UserResource user,
                                            Model model) {

        if (cannotSelectOrganisation(user, request)) {
            return "redirect:" + nextPageInFlow();
        }

        CompetitionResource competition = competitionRestService.getCompetitionById(getCompetitionIdFromInviteOrCookie(request)).getSuccess();

        OrganisationSelectionViewModel viewModel = organisationSelectionViewModelPopulator.populate(user,
                request, competition,
                nextPageInFlow());

        if (viewModel.getChoices().isEmpty()) {
            return "redirect:" + nextPageInFlow();
        }

        model.addAttribute("model", viewModel);
        addPageSubtitleToModel(request, user, model);

        return "registration/organisation/select-organisation";
    }

    @PreAuthorize("hasPermission(#user,'APPLICATION_CREATION')")
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

    private boolean cannotSelectOrganisation(UserResource user, HttpServletRequest request) {
        return user == null
                || !user.hasRole(Role.APPLICANT);
    }

    private String nextPageInFlow() {
        return "/organisation/create/organisation-type";
    }

    private Supplier<String> validateEligibility(HttpServletRequest request, HttpServletResponse response, UserResource user, OrganisationSelectionForm form) {
        return () -> {
            if (registrationCookieService.isLeadJourney(request)) {
                if (!validateLeadApplicant(request, form))
                    return "redirect:" + BASE_URL + "/" + ORGANISATION_TYPE + "/" + NOT_ELIGIBLE;
            }

            if (registrationCookieService.isCollaboratorJourney(request)) {
                if (!validateCollaborator(request, form)) {
                    return "redirect:" + BASE_URL + "/" + ORGANISATION_TYPE + "/" + NOT_ELIGIBLE;
                }
            }

            if (newOrganisationSearchEnabled && isDeprecatedManualEntry(form)) {
                return "redirect:" + BASE_URL + "/" + EXISTING_ORGANISATION + "/" + form.getSelectedOrganisationId();
            }

            return organisationJourneyEnd.completeProcess(request, response, user, form.getSelectedOrganisationId());
        };
    }

    private boolean isDeprecatedManualEntry(OrganisationSelectionForm form) {
        OrganisationResource selectedOrganisation = organisationRestService.getOrganisationById(form.getSelectedOrganisationId()).getSuccess();

        return selectedOrganisation.getCompaniesHouseNumber() == null
                && selectedOrganisation.getOrganisationNumber() == null
                // Main way to distinguish old manual entry is lack of address.
                && selectedOrganisation.getAddresses().isEmpty()
                && selectedOrganisation.getOrganisationTypeEnum() != OrganisationTypeEnum.RESEARCH
                && selectedOrganisation.getOrganisationTypeEnum() != OrganisationTypeEnum.KNOWLEDGE_BASE
                && !selectedOrganisation.isInternational();
    }

    private boolean validateCollaborator(HttpServletRequest request, OrganisationSelectionForm form) {
        CompetitionResource competition = competitionRestService.getCompetitionById(getCompetitionIdFromInviteOrCookie(request)).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(form.getSelectedOrganisationId()).getSuccess();

        if (competition.isKtp()) {
            return isValidKtpCollaborator(organisation.getOrganisationType());
        } else {
            return isValidCollaborator(organisation.getOrganisationType());
        }
    }

    private boolean validateLeadApplicant(HttpServletRequest request, OrganisationSelectionForm form) {
        CompetitionResource competition = competitionRestService.getCompetitionById(registrationCookieService.getCompetitionIdCookieValue(request).get()).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(form.getSelectedOrganisationId()).getSuccess();

        return competition.getLeadApplicantTypes().contains(organisation.getOrganisationType());
    }
}