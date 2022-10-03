package org.innovateuk.ifs.management.competition.setup.projectimpact.controller;

import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionApplicationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.projectimpact.form.ProjectImpactForm;
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
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.PROJECT_IMPACT;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.management.competition.setup.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;

/**
 * Controller to manage the Organisational eligibility and Lead organisations in competition setup process
 */
@Controller
@RequestMapping("/competition/setup/{competitionId}/section/project-impact")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = SupportingDocumentController.class)
@PreAuthorize("hasAnyAuthority('comp_admin')")
public class SupportingDocumentController {


    private static final String MODEL = "model";

    private final CompetitionRestService competitionRestService;

    private final CompetitionSetupService competitionSetupService;
    private final CompetitionApplicationConfigRestService competitionApplicationConfigRestService;

    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
   public SupportingDocumentController(CompetitionRestService competitionRestService, CompetitionSetupService competitionSetupService, CompetitionApplicationConfigRestService competitionApplicationConfigRestService, CompetitionSetupRestService competitionSetupRestService) {
        this.competitionRestService = competitionRestService;
        this.competitionSetupService = competitionSetupService;
        this.competitionApplicationConfigRestService = competitionApplicationConfigRestService;
        this.competitionSetupRestService = competitionSetupRestService;
    }


    @GetMapping
    public String organisationalEligibilityPageDetails(Model model,
                                                       @PathVariable long competitionId,
                                                       UserResource loggedInUser) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, PROJECT_IMPACT));
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupService.getSectionFormPopulator(PROJECT_IMPACT).populateForm(competition));

        return "competition/setup";
    }

    @PostMapping
    public String submitOrganisationalEligibilitySectionDetails(@Valid @ModelAttribute("competitionSetupForm") ProjectImpactForm projectImpactForm,
                                                                BindingResult bindingResult,
                                                                ValidationHandler validationHandler,
                                                                @PathVariable("competitionId") long competitionId,
                                                                UserResource loggedInUser,
                                                                Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();


        Supplier<String> failureView = () -> {
            model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, PROJECT_IMPACT));
            model.addAttribute(COMPETITION_SETUP_FORM_KEY, projectImpactForm);
            return "competition/setup";
        };
        Supplier<String> successView = () -> {
            model.addAttribute("model.general.editable",false);
            return validationHandler.addAnyErrors(saveImpactManagementConfig(competition, projectImpactForm), fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () ->
                            format("redirect:/competition/setup/%d/section/%s", competition.getId(), PROJECT_IMPACT.getPostMarkCompletePath()));
        };
        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    private ServiceResult<Void> saveImpactManagementConfig(CompetitionResource competition, @Valid ProjectImpactForm projectImpactForm) {
        competitionSetupRestService.markSectionComplete(competition.getId(),PROJECT_IMPACT);
        CompetitionApplicationConfigResource competitionApplicationConfigResource = competitionApplicationConfigRestService.findOneByCompetitionId(competition.getId()).getSuccess();
        competitionApplicationConfigResource.setImSurveyRequired(projectImpactForm.getProjectImpactSurveyApplicable());
        return competitionApplicationConfigRestService.updateImpactSurvey(competition.getId(), competitionApplicationConfigResource).toServiceResult().andOnSuccessReturnVoid();
    }

}