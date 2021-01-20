package org.innovateuk.ifs.management.assessmentperiod.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodMilestonesForm;
import org.innovateuk.ifs.management.assessmentperiod.populator.ManageAssessmentPeriodsPopulator;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Controller
@RequestMapping("/assessment/competition/{competitionId}/assessment-period")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssessmentPeriodController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSMENT')")
public class AssessmentPeriodController {

    @Autowired
    private ManageAssessmentPeriodsPopulator assessmentPeriodsPopulator;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private MilestoneRestService milestoneRestService;

    @GetMapping
    public String manageAssessmentPeriods(@ModelAttribute(value = "form") AssessmentPeriodForm form,
                                          @PathVariable long competitionId, Model model) {

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

//        Map<Long, List<MilestoneResource>> assessmentPeriods = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess()
//                .stream()
//                .filter(milestone -> milestone.getAssessmentPeriodId() != null)
//                .collect(Collectors.groupingBy(MilestoneResource::getAssessmentPeriodId));
//
//        List<AssessmentPeriodMilestonesForm> milestonesForms = new ArrayList<>();
//        assessmentPeriods.forEach((key, value) -> {
//            List<MilestoneRowForm> milestoneFormEntries = new ArrayList();
//            value.stream().forEachOrdered(milestone ->
//                    milestoneFormEntries.add(populateMilestoneFormEntries(milestone, competitionResource))
//            );
//            AssessmentPeriodMilestonesForm milestonesForm = new AssessmentPeriodMilestonesForm();
//            milestonesForm.setMilestoneEntries(milestoneFormEntries);
//            milestonesForms.add(milestonesForm);
//        });
//
//        form.addExistingAssessmentPeriods(milestonesForms);

        model.addAttribute("model", assessmentPeriodsPopulator.populateModel(competitionId));
        return "competition/manage-assessment-periods";
    }

    @PostMapping
    public String submitAssessmentPeriods(@ModelAttribute(value = "form", binding = true) AssessmentPeriodForm form,
                                          BindingResult bindingResult,
                                          ValidationHandler validationHandler,
                                          Model model,
                                          @PathVariable long competitionId
    ) {


        return " ";
    }

    @PostMapping(params = "add-assessment-period")
    public String addAssessmentPeriod(@ModelAttribute(value = "form") AssessmentPeriodForm form,
                                      BindingResult bindingResult,
                                      ValidationHandler validationHandler,
                                      Model model,
                                      @PathVariable long competitionId
    ) {
        AssessmentPeriodMilestonesForm newAssessmentPeriod = new AssessmentPeriodMilestonesForm();

        List<MilestoneRowForm> milestoneRowForms = new ArrayList<>();
        MilestoneRowForm assessorBriefing = new MilestoneRowForm();
        assessorBriefing.setMilestoneType(MilestoneType.ASSESSOR_BRIEFING);
        milestoneRowForms.add(assessorBriefing);
        MilestoneRowForm acceptanceDeadline = new MilestoneRowForm();
        acceptanceDeadline.setMilestoneType(MilestoneType.ASSESSOR_ACCEPTS);
        milestoneRowForms.add(acceptanceDeadline);
        MilestoneRowForm assessmentDeadline = new MilestoneRowForm();
        assessmentDeadline.setMilestoneType(MilestoneType.ASSESSOR_DEADLINE);
        milestoneRowForms.add(assessmentDeadline);

        newAssessmentPeriod.setMilestoneEntries(milestoneRowForms);
        form.addNewAssessmentPeriod(newAssessmentPeriod);

        model.addAttribute("form", form);
        return manageAssessmentPeriods(form, competitionId, model);
    }

    private String redirectToManageAssessmentPeriods(long competitionId) {
        return format("redirect:/assessment/competition/%s/assessment-period", competitionId);
    }

    private MilestoneRowForm populateMilestoneFormEntries(MilestoneResource milestone, CompetitionResource competitionResource) {
        return new MilestoneRowForm(milestone.getType(), milestone.getDate(), isEditable(milestone, competitionResource));
    }

    private boolean isEditable(MilestoneResource milestone, CompetitionResource competitionResource) {
        return !competitionResource.isSetupAndLive() || milestone.getDate().isAfter(ZonedDateTime.now());
    }

}
