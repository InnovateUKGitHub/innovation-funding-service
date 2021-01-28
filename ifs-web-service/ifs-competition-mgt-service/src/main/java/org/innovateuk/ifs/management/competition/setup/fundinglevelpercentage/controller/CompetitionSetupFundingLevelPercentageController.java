package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelPercentageForm;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.populator.FundingLevelPercentageFormPopulator;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.sectionupdater.FundingLevelPercentageSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.validator.FundingLevelPercentageValidator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.FUNDING_LEVEL_PERCENTAGE;
import static org.innovateuk.ifs.competition.resource.FundingRules.STATE_AID;
import static org.innovateuk.ifs.management.competition.setup.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;

/**
 * Controller to manage the Funding Level Percentages in competition setup process
 */
@Controller
@RequestMapping("/competition/setup/{competitionId}/section/funding-level-percentage")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionSetupFundingLevelPercentageController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionSetupFundingLevelPercentageController {

    private static final String MODEL = "model";

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private FundingLevelPercentageValidator fundingLevelPercentageValidator;

    @Autowired
    private FundingLevelPercentageSectionUpdater updater;

    @Value("${ifs.subsidy.control.northern.ireland.enabled}")
    private boolean northernIrelandSubsidyControlToggle;

    @GetMapping
    public String fundingLevelPercentage(Model model,
                                         @PathVariable long competitionId,
                                         UserResource loggedInUser) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        if (competition.getFundingRules() == FundingRules.SUBSIDY_CONTROL && northernIrelandSubsidyControlToggle) {
            CompetitionSetupViewModel viewModel = competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, FUNDING_LEVEL_PERCENTAGE);
            if (viewModel.getGeneral().isEditable()) {
                return format("redirect:/competition/setup/%d/section/%s/fundingRule/%s", competition.getId(), FUNDING_LEVEL_PERCENTAGE.getPostMarkCompletePath(), competition.getFundingRules().name());
            }
        }
        return view(model, competition, loggedInUser, null);
    }

    @GetMapping("/fundingRule/{fundingRules}")
    public String fundingLevelPercentage(Model model,
                                         @PathVariable long competitionId,
                                         @PathVariable FundingRules fundingRules,
                                         UserResource loggedInUser) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        return view(model, competition, loggedInUser, fundingRules);
    }

    @PostMapping
    public String submitFundingLevelPercentageSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) FundingLevelPercentageForm competitionSetupForm,
                                                             BindingResult bindingResult,
                                                             ValidationHandler validationHandler,
                                                             @PathVariable long competitionId,
                                                             UserResource loggedInUser,
                                                             Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        fundingLevelPercentageValidator.validate(competitionSetupForm, validationHandler);

        Supplier<String> failureView = () -> view(model, competition, loggedInUser, null);
        Supplier<String> successView = () ->
                        validationHandler.addAnyErrors(competitionSetupService.saveCompetitionSetupSection(competitionSetupForm, competition, FUNDING_LEVEL_PERCENTAGE))
                                .failNowOrSucceedWith(failureView, () ->
                                        format("redirect:/competition/setup/%d/section/%s", competition.getId(), FUNDING_LEVEL_PERCENTAGE.getPostMarkCompletePath()));
        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    @PostMapping("/fundingRule/{fundingRules}")
    public String submitFundingRulesPercentages(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) FundingLevelPercentageForm competitionSetupForm,
                                                 BindingResult bindingResult,
                                                 ValidationHandler validationHandler,
                                                 @PathVariable long competitionId,
                                                 @PathVariable FundingRules fundingRules,
                                                 UserResource loggedInUser,
                                                 Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        fundingLevelPercentageValidator.validate(competitionSetupForm, validationHandler);

        Supplier<String> failureView = () -> view(model, competition, loggedInUser, null);
        Supplier<String> successView = () ->
                fundingRules == FundingRules.STATE_AID ?
                        validationHandler.addAnyErrors(competitionSetupService.saveCompetitionSetupSection(competitionSetupForm, competition, FUNDING_LEVEL_PERCENTAGE))
                                .failNowOrSucceedWith(failureView, () ->
                                        format("redirect:/competition/setup/%d/section/%s", competition.getId(), FUNDING_LEVEL_PERCENTAGE.getPostMarkCompletePath()))
                        :
                        validationHandler.addAnyErrors(updater.saveSection(competition, competitionSetupForm))
                                .failNowOrSucceedWith(failureView, () ->
                                        format("redirect:/competition/setup/%d/section/%s/fundingRule/%s", competition.getId(), FUNDING_LEVEL_PERCENTAGE.getPath(), STATE_AID.name()));

        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }
    private String view(Model model,
                        CompetitionResource competition,
                        UserResource loggedInUser,
                        FundingRules fundingRules) {

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competition.getId())) {
            return "redirect:/competition/setup/" + competition.getId();
        }
        CompetitionSetupViewModel viewModel = competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, FUNDING_LEVEL_PERCENTAGE);
        FundingLevelPercentageFormPopulator populator = (FundingLevelPercentageFormPopulator) competitionSetupService.getSectionFormPopulator(FUNDING_LEVEL_PERCENTAGE);
        CompetitionSetupForm form;
        if (viewModel.getGeneral().isEditable()) {
            form = populator.populateForm(competition, fundingRules);
        } else {
            form = populator.populateForm(competition);
        }
        model.addAttribute(MODEL, viewModel);
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, form);
        model.addAttribute("fundingRules", fundingRules);

        return "competition/setup";
    }

}