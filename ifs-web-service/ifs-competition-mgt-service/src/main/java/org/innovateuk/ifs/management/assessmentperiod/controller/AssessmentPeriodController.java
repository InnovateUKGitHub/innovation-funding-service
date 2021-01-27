package org.innovateuk.ifs.management.assessmentperiod.controller;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodMilestonesForm;
import org.innovateuk.ifs.management.assessmentperiod.populator.ManageAssessmentPeriodsPopulator;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    private CompetitionRestService competitionRestService;

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Autowired
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    @GetMapping
    public String manageAssessmentPeriods(@ModelAttribute(value = "form") AssessmentPeriodForm form,
                                          @PathVariable long competitionId, Model model) {

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess();
        Map<Long, List<MilestoneResource>> existingAssessmentPeriods = milestones
                .stream()
                .filter(milestone -> milestone.getAssessmentPeriodId() != null)
                .collect(Collectors.groupingBy(MilestoneResource::getAssessmentPeriodId));

        List<AssessmentPeriodMilestonesForm> milestonesForms = new ArrayList<>();
        existingAssessmentPeriods.forEach((key, value) -> {
            LinkedMap<String, MilestoneRowForm> milestoneFormEntries = new LinkedMap<>();
            value.stream().forEachOrdered(milestone ->
                    milestoneFormEntries.put(milestone.getType().getMilestoneDescription(), populateMilestoneFormEntries(milestone))
            );
            AssessmentPeriodMilestonesForm milestonesForm = new AssessmentPeriodMilestonesForm();
            milestonesForm.setAssessmentPeriodId(key);
            milestonesForm.setMilestoneEntries(milestoneFormEntries);
            milestonesForms.add(milestonesForm);
        });
        form.addExistingAssessmentPeriods(milestonesForms);
//        List<AssessmentPeriodForm> newAssessmentPeriods = milestones.stream().filter(ap ->
//                ap.getType().equals(MilestoneType.ASSESSOR_BRIEFING) ||
//                        ap.getType().equals(MilestoneType.ASSESSOR_ACCEPTS) ||
//                        ap.getType().equals(MilestoneType.ASSESSOR_DEADLINE)
//        ).flatMap()
//                .collect(Collectors.toList());

        model.addAttribute("model", assessmentPeriodsPopulator.populateModel(competitionId));
        return "competition/manage-assessment-periods";
    }

    @PostMapping
    public String submitAssessmentPeriods(@ModelAttribute(value = "form", binding = true) AssessmentPeriodForm form,
                                          BindingResult bindingResult,
                                          ValidationHandler validationHandler,
                                          Model model,
                                          @PathVariable long competitionId,
                                          UserResource loggedInUser
    ) {

        if (bindingResult.hasErrors()) {
            LOG.error(bindingResult.getAllErrors().toString());
        }

        List<MilestoneResource> existingMilestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess()
                .stream().filter(existingMilestone -> {
                            return existingMilestone.getType().equals(MilestoneType.ASSESSOR_BRIEFING) ||
                                    existingMilestone.getType().equals(MilestoneType.ASSESSOR_ACCEPTS) ||
                                    existingMilestone.getType().equals(MilestoneType.ASSESSOR_DEADLINE);
                        }
                ).collect(Collectors.toList());
        Map<Long, Collection<MilestoneRowForm>> formMilestones = new HashMap<>();
        form.getFormList().forEach(assessmentPeriodForm -> {
            Long assessmentPeriodId = assessmentPeriodForm.getAssessmentPeriodId();
            formMilestones.put(assessmentPeriodId, assessmentPeriodForm.getMilestoneEntries().values());
        });

        // group the existing by id and match? Or just push straight?
        List<MilestoneResource> updatedMilestones = new ArrayList<>();
        existingMilestones.stream()
                .filter(this::isEditable)
                .forEach(existingMilestone -> {

            MilestoneRowForm milestoneWithUpdate = formMilestones.get(existingMilestone.getAssessmentPeriodId())
                    .stream().filter(e -> e.getMilestoneType().equals(existingMilestone.getType())).findFirst().orElse(null);

            if (milestoneWithUpdate != null) {
                ZonedDateTime temp = milestoneWithUpdate.getMilestoneAsZonedDateTime();
                if (temp != null) {
                    existingMilestone.setDate(temp);
                    updatedMilestones.add(existingMilestone);
                } else {
                    milestoneRestService
                            .resetMilestone(existingMilestone)
                            .getSuccess();
                }
            }
        });

        Supplier<String> successView = () -> redirectToManageAssessment(competitionId);
        Supplier<String> failureView = () -> {
//            model.addAttribute("model", competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, section));
//            return "competition/manage-assessment-periods";

            return manageAssessmentPeriods(form, competitionId, model);
        };
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> saveResult = milestoneRestService.updateAssessmentPeriodMilestones(updatedMilestones);
            return validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, successView);
        });
    }

    @PostMapping(params = "add-assessment-period")
    public String addAssessmentPeriod(@ModelAttribute(value = "form") AssessmentPeriodForm form,
                                      BindingResult bindingResult,
                                      ValidationHandler validationHandler,
                                      Model model,
                                      @PathVariable long competitionId
    ) {
        milestoneRestService.addNewAssessmentPeriod(competitionId);
        return redirectToManageAssessmentPeriods(competitionId);
    }

    private String redirectToManageAssessment(long competitionId) {
        return format("redirect:/assessment/competition/%s", competitionId);
    }

    private String redirectToManageAssessmentPeriods(long competitionId) {
        return format("redirect:/assessment/competition/%s/assessment-period", competitionId);
    }

    private MilestoneRowForm populateMilestoneFormEntries(MilestoneResource milestone) {
        return new MilestoneRowForm(milestone.getType(), milestone.getDate(), isEditable(milestone));
    }

    private boolean isEditable(MilestoneResource milestone) {
        return milestone.getDate().isAfter(ZonedDateTime.now());
    }

}
