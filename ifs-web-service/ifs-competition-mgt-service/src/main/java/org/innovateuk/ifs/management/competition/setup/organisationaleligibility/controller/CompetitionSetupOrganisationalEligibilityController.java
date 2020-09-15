package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form.LeadInternationalOrganisationForm;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form.OrganisationalEligibilityForm;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.populator.LeadInternationalOrganisationFormPopulator;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.populator.LeadInternationalOrganisationViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.management.competition.setup.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;

/**
 * Controller to manage the Organisational eligibility and Lead organisations in competition setup process
 */
@Controller
@RequestMapping("/competition/setup/{competitionId}/section/organisational-eligibility")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionSetupOrganisationalEligibilityController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionSetupOrganisationalEligibilityController {

    public static final String ORGANISATIONAL_ELIGIBILITY_LANDING_REDIRECT = "redirect:/competition/setup/%d/section/organisational-eligibility";
    private static final String MODEL = "model";

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @Autowired
    private LeadInternationalOrganisationViewModelPopulator leadInternationalOrganisationViewModelPopulator;

    @Autowired
    private LeadInternationalOrganisationFormPopulator leadInternationalOrganisationFormPopulator;

    @GetMapping
    public String organisationalEligibilityPageDetails(Model model,
                                                       @PathVariable long competitionId,
                                                       UserResource loggedInUser) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, ORGANISATIONAL_ELIGIBILITY));
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupService.getSectionFormData(competition, ORGANISATIONAL_ELIGIBILITY));

        return "competition/setup";
    }

    @PostMapping
    public String submitOrganisationalEligibilitySectionDetails(@Valid @ModelAttribute("competitionSetupForm") OrganisationalEligibilityForm organisationalEligibilityForm,
                                                                BindingResult bindingResult,
                                                                ValidationHandler validationHandler,
                                                                @PathVariable("competitionId") long competitionId,
                                                                UserResource loggedInUser,
                                                                Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        Supplier<String> failureView = () -> {
            model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, ORGANISATIONAL_ELIGIBILITY));
            model.addAttribute(COMPETITION_SETUP_FORM_KEY, organisationalEligibilityForm);
            return "competition/setup";
        };

        Supplier<String> successView = () ->
                Boolean.TRUE.equals(organisationalEligibilityForm.getInternationalOrganisationsApplicable()) ?

                        validationHandler.addAnyErrors(saveOrganisationEligibility(organisationalEligibilityForm, competition), fieldErrorsToFieldErrors(), asGlobalErrors())
                                .failNowOrSucceedWith(failureView, () ->
                                        format("redirect:/competition/setup/%d/section/%s/lead-international-organisation", competition.getId(), ORGANISATIONAL_ELIGIBILITY.getPostMarkCompletePath())) :

                        validationHandler.addAnyErrors(saveResult(organisationalEligibilityForm, competition), fieldErrorsToFieldErrors(), asGlobalErrors())
                                .failNowOrSucceedWith(failureView, () ->
                                        format("redirect:/competition/setup/%d/section/%s", competition.getId(), ORGANISATIONAL_ELIGIBILITY.getPostMarkCompletePath()));

        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    @GetMapping("/lead-international-organisation")
    public String viewLeadInternationalOrganisationDetails(@PathVariable("competitionId") long competitionId,
                                                           @ModelAttribute LeadInternationalOrganisationForm leadInternationalOrganisationForm,
                                                           Model model) {

        RestResult<CompetitionOrganisationConfigResource> configResource = competitionOrganisationConfigRestService.findByCompetitionId(competitionId);
        model.addAttribute("model", leadInternationalOrganisationViewModelPopulator.populateModel(competitionId, configResource.getSuccess()));
        model.addAttribute("leadInternationalOrganisationForm", leadInternationalOrganisationFormPopulator.populateForm(configResource.getSuccess()));

        return "competition/setup/lead-international-organisation";
    }

    @PostMapping("/lead-international-organisation")
    public String submitLeadInternationalOrganisationDetails(@PathVariable("competitionId") long competitionId,
                                                             @Valid @ModelAttribute LeadInternationalOrganisationForm leadInternationalOrganisationForm,
                                                             BindingResult bindingResult,
                                                             ValidationHandler validationHandler,
                                                             Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionOrganisationConfigResource configResource = competitionOrganisationConfigRestService.findByCompetitionId(competitionId).getSuccess();

        Supplier<String> failureView = () -> {
            model.addAttribute("model", leadInternationalOrganisationViewModelPopulator.populateModel(competition.getId(), configResource));
            return "competition/setup/lead-international-organisation";
        };

        Supplier<String> successView = () -> {
            return validationHandler.addAnyErrors(saveOrganisationConfig(competition, leadInternationalOrganisationForm), fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () ->
                            format("redirect:/competition/setup/%d/section/%s", competition.getId(), ORGANISATIONAL_ELIGIBILITY.getPostMarkCompletePath()));
        };

        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    private ServiceResult<Void> saveOrganisationConfig(CompetitionResource competition, LeadInternationalOrganisationForm leadInternationalOrganisationForm) {

        CompetitionOrganisationConfigResource competitionOrganisationConfigResource = competitionOrganisationConfigRestService.findByCompetitionId(competition.getId()).getSuccess();
        competitionOrganisationConfigResource.setInternationalLeadOrganisationAllowed(leadInternationalOrganisationForm.getLeadInternationalOrganisationsApplicable());

        OrganisationalEligibilityForm form = new OrganisationalEligibilityForm();
        form.setInternationalOrganisationsApplicable(competitionOrganisationConfigResource.getInternationalOrganisationsAllowed());
        form.setLeadInternationalOrganisationsApplicable(competitionOrganisationConfigResource.getInternationalLeadOrganisationAllowed());

        return competitionSetupService.saveCompetitionSetupSection(form, competition, ORGANISATIONAL_ELIGIBILITY);
    }

    private ServiceResult<Void> saveOrganisationEligibility(OrganisationalEligibilityForm organisationalEligibilityForm, CompetitionResource competition) {

        CompetitionOrganisationConfigResource competitionOrganisationConfigResource = competitionOrganisationConfigRestService.findByCompetitionId(competition.getId()).getSuccess();
        competitionOrganisationConfigResource.setInternationalOrganisationsAllowed(organisationalEligibilityForm.getInternationalOrganisationsApplicable());
        return competitionOrganisationConfigRestService.update(competition.getId(), competitionOrganisationConfigResource).toServiceResult().andOnSuccessReturnVoid();
    }

    private ServiceResult<Void> saveResult(OrganisationalEligibilityForm organisationalEligibilityForm, CompetitionResource competition) {
        return competitionSetupService.saveCompetitionSetupSection(organisationalEligibilityForm, competition, ORGANISATIONAL_ELIGIBILITY);
    }
}