package org.innovateuk.ifs.management.assessmentperiod.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.assessmentperiod.form.ManageAssessmentPeriodsForm;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;
import org.innovateuk.ifs.management.assessmentperiod.populator.ManageAssessmentPeriodsPopulator;
import org.innovateuk.ifs.management.assessmentperiod.service.AssessmentPeriodService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

@Controller
@RequestMapping("/assessment/competition/{competitionId}/assessment-period")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssessmentPeriodController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSMENT')")
public class AssessmentPeriodController {

    private static final Logger LOG = LoggerFactory.getLogger(AssessmentPeriodController.class);

    @Autowired
    private ManageAssessmentPeriodsPopulator assessmentPeriodsPopulator;

    @Autowired
    private AssessmentPeriodRestService assessmentPeriodRestService;

    @Autowired
    private AssessmentPeriodService assessmentPeriodService;

    @GetMapping
    public String manageAssessmentPeriods(@ModelAttribute(value = "form") ManageAssessmentPeriodsForm form,
                                          @PathVariable long competitionId, Model model) {

        List<AssessmentPeriodForm> milestonesForms = assessmentPeriodService.getAssessmentPeriodMilestonesForms(competitionId);
        form.addExistingAssessmentPeriods(milestonesForms);
        model.addAttribute("model", assessmentPeriodsPopulator.populateModel(competitionId));
        return "competition/manage-assessment-periods";
    }

    @PostMapping
    public String submitAssessmentPeriods(@ModelAttribute(value = "form", binding = true) ManageAssessmentPeriodsForm form,
                                          BindingResult bindingResult,
                                          ValidationHandler validationHandler,
                                          Model model,
                                          @PathVariable long competitionId,
                                          UserResource loggedInUser
    ) {

        if (bindingResult.hasErrors()) {
            LOG.error(bindingResult.getAllErrors().toString());
        }

        List<MilestoneResource> updatedMilestones = assessmentPeriodService.extractMilestoneResourcesFromForm(form, competitionId);

        Supplier<String> successView = () -> redirectToManageAssessment(competitionId);
        Supplier<String> failureView = () -> manageAssessmentPeriods(form, competitionId, model);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> saveResult = assessmentPeriodRestService.updateAssessmentPeriodMilestones(updatedMilestones);
            return validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, successView);
        });
    }

    @PostMapping(params = "add-assessment-period")
    public String addAssessmentPeriod(@ModelAttribute(value = "form") ManageAssessmentPeriodsForm form,
                                      BindingResult bindingResult,
                                      ValidationHandler validationHandler,
                                      Model model,
                                      @PathVariable long competitionId
    ) {
        assessmentPeriodRestService.addNewAssessmentPeriod(competitionId);
        return redirectToManageAssessmentPeriods(competitionId);
    }

    private String redirectToManageAssessment(long competitionId) {
        return format("redirect:/assessment/competition/%s", competitionId);
    }

    private String redirectToManageAssessmentPeriods(long competitionId) {
        return format("redirect:/assessment/competition/%s/assessment-period", competitionId);
    }

}
