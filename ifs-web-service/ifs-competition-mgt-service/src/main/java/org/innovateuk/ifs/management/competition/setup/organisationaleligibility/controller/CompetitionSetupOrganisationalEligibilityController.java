package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form.OrganisationalEligibilityForm;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form.LeadInternationalOrganisationForm;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.populator.LeadInternationalOrganisationViewModelPopulator;
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
 * Controller to manage the Application Questions and it's sub-sections in the
 * competition setup process
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
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @Autowired
    private LeadInternationalOrganisationViewModelPopulator leadInternationalOrganisationViewModelPopulator;

    @GetMapping
    public String organisationalEligibilityPageDetails(Model model, @PathVariable long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, ORGANISATIONAL_ELIGIBILITY));
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupService.getSectionFormData(competition, ORGANISATIONAL_ELIGIBILITY));

        return "competition/setup";
    }

    @PostMapping
    public String submitOrganisationalEligibilitySectionDetails(@Valid @ModelAttribute("competitionSetupForm") OrganisationalEligibilityForm competitionSetupForm,
                                                                BindingResult bindingResult,
                                                                ValidationHandler validationHandler,
                                                                @PathVariable("competitionId") long competitionId,
                                                                Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        Supplier<String> successView = () ->
                competitionSetupForm.getInternationalOrganisationsApplicable() ?
                        format("redirect:/competition/setup/%d/section/%s/lead-international-organisation", competition.getId(), ORGANISATIONAL_ELIGIBILITY.getPostMarkCompletePath()) :
                        format("redirect:/competition/setup/%d/section/%s", competition.getId(), ORGANISATIONAL_ELIGIBILITY.getPostMarkCompletePath());


        Supplier<String> failureView = () -> {
            model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, ORGANISATIONAL_ELIGIBILITY));
            model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupForm);
            return "competition/setup";
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> saveResult = competitionSetupService.saveCompetitionSetupSection(competitionSetupForm, competition, ORGANISATIONAL_ELIGIBILITY);
            return validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, successView);
        });
    }

    @GetMapping("/lead-international-organisation")
    public String viewLeadInternationalOrganisationDetails(@PathVariable("competitionId") long competitionId,
                                                           @ModelAttribute LeadInternationalOrganisationForm leadInternationalOrganisationForm,
                                                           Model model) {

        RestResult<CompetitionOrganisationConfigResource> configResource = competitionOrganisationConfigRestService.findByCompetitionId(competitionId);
        model.addAttribute("model", leadInternationalOrganisationViewModelPopulator.populateModel(competitionId, configResource.getSuccess()));

        return "competition/setup/lead-international-organisation";
    }

    @PostMapping("/lead-international-organisation")
    public String submitLeadInternationalOrganisationDetails(@PathVariable("competitionId") long competitionId,
                                                             @Valid @ModelAttribute LeadInternationalOrganisationForm leadInternationalOrganisationForm,
                                                             BindingResult bindingResult,
                                                             ValidationHandler validationHandler,
                                                             Model model) {

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionOrganisationConfigResource configResource = competitionOrganisationConfigRestService.findByCompetitionId(competitionId).getSuccess();

        Supplier<String> successView = () -> format("redirect:/competition/setup/%d/section/organisational-eligibility", competitionResource.getId());

        Supplier<String> failureView = () -> {
            model.addAttribute("model", leadInternationalOrganisationViewModelPopulator.populateModel(competitionResource.getId(), configResource));
            return "competition/setup/lead-international-organisation";
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> saveResult = doSaveSection(competitionResource, leadInternationalOrganisationForm);
            return validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, successView);
        });
    }

    private ServiceResult<Void> doSaveSection(CompetitionResource competition, LeadInternationalOrganisationForm leadInternationalOrganisationForm) {

        CompetitionOrganisationConfigResource competitionOrganisationConfigResource = competitionOrganisationConfigRestService.findByCompetitionId(competition.getId()).getSuccess();
        competitionOrganisationConfigResource.setInternationalLeadOrganisationAllowed(leadInternationalOrganisationForm.getLeadInternationalOrganisationsApplicable());

        return competitionOrganisationConfigRestService.update(competition.getId(), competitionOrganisationConfigResource).toServiceResult().andOnSuccessReturnVoid();
    }
}