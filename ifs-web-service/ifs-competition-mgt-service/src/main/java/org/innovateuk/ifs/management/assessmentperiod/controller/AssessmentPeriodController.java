package org.innovateuk.ifs.management.assessmentperiod.controller;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.commons.resource.PageResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;
import org.innovateuk.ifs.management.assessmentperiod.form.ManageAssessmentPeriodsForm;
import org.innovateuk.ifs.management.assessmentperiod.populator.AssessmentPeriodFormPopulator;
import org.innovateuk.ifs.management.assessmentperiod.populator.ManageAssessmentPeriodsPopulator;
import org.innovateuk.ifs.management.assessmentperiod.saver.AssessmentPeriodSaver;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.LongStream.range;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

@Controller
@RequestMapping("/assessment/competition/{competitionId}/assessment-period")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssessmentPeriodController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSMENT')")
public class AssessmentPeriodController {
    private static Integer PAGE_SIZE = 6;

    private static final Logger LOG = LoggerFactory.getLogger(AssessmentPeriodController.class);

    @Autowired
    private ManageAssessmentPeriodsPopulator assessmentPeriodsPopulator;

    @Autowired
    private AssessmentPeriodRestService assessmentPeriodRestService;

    @Autowired
    private AssessmentPeriodSaver saver;

    @Autowired
    private AssessmentPeriodFormPopulator formPopulator;

    @GetMapping
    public String manageAssessmentPeriods(@RequestParam(value = "addAssessment", defaultValue = "false") boolean addAssessment,
                                          @PathVariable long competitionId,
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          Model model) {
        PageResource<AssessmentPeriodResource> actualPageResult = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId, page - 1, PAGE_SIZE).getSuccess();
        ManageAssessmentPeriodsForm form = formPopulator.populate(competitionId, actualPageResult , addAssessment);
        if (addAssessment || actualPageResult.getTotalElements() == 0){
            // Add an empty assessment period to the end of the form for the user to fill in.
            AssessmentPeriodForm newAssessmentPeriodForm = newAssessmentPeriodForm();
            newAssessmentPeriodForm.setIndex((int)actualPageResult.getTotalElements() + 1);
            form.getAssessmentPeriods().add(newAssessmentPeriodForm);
            PageResource<AssessmentPeriodResource> pageResultWithUnsavedAssessmentPeriods = actualPageResult.pageResourceWithDummyItemAddedToLastPage(new AssessmentPeriodResource());
            return view(competitionId, form, pageResultWithUnsavedAssessmentPeriods, model);
        }
        else {
            return view(competitionId, form, actualPageResult , model);
        }
    }

    private AssessmentPeriodForm newAssessmentPeriodForm(){
        AssessmentPeriodForm form = new AssessmentPeriodForm();
        LinkedMap<String, MilestoneRowForm> newMilestones = new LinkedMap<>();
        for (MilestoneType milestoneType : EnumSet.of(ASSESSOR_BRIEFING, ASSESSORS_NOTIFIED, ASSESSOR_ACCEPTS, ASSESSOR_DEADLINE, ASSESSMENT_CLOSED)){
            MilestoneRowForm milestoneRowForm = new MilestoneRowForm();
            milestoneRowForm.setEditable(true);
            milestoneRowForm.setMilestoneType(milestoneType);
            newMilestones.put(milestoneType.name(), milestoneRowForm);
        }
        form.setMilestoneEntries(newMilestones);
        return form;
    }

    @PostMapping
    public String submitAssessmentPeriodsAndReturnToManageAssessments(@Valid @ModelAttribute(value = "form") ManageAssessmentPeriodsForm form,
                                          @SuppressWarnings("unused") BindingResult bindingResult,
                                          ValidationHandler validationHandler,
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          Model model,
                                          @PathVariable long competitionId) {
        Supplier<String> successView = () -> redirectToManageAssessment(competitionId);
        return submit(form, validationHandler, page, model, competitionId, successView);
    }

    @PostMapping(params = "add-assessment-period")
    public String addAssessmentPeriod(@Valid @ModelAttribute(value = "form") ManageAssessmentPeriodsForm form,
                                      @SuppressWarnings("unused") BindingResult bindingResult,
                                      ValidationHandler validationHandler,
                                      @RequestParam(value = "page", defaultValue = "1") int page,
                                      Model model,
                                      @PathVariable long competitionId) {
        Supplier<String> successView = () -> redirectToLastPageToAddAssessmentPeriod(competitionId);
        return submit(form, validationHandler, page, model, competitionId, successView);
    }

    private String submit(ManageAssessmentPeriodsForm form,
                          ValidationHandler validationHandler,
                          int page,
                          Model model,
                          long competitionId,
                          Supplier<String> successView) {
        form.orderMilestoneEnties();
        Supplier<String> failureView = () -> reshowPage(form, page, model, competitionId);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> saveResult = saver.save(competitionId, form);
            return  validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, successView);
        });
    }

    private String reshowPage(ManageAssessmentPeriodsForm form, int page, Model model, long competitionId){
        PageResource<AssessmentPeriodResource> actualPageResult
                = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId, page - 1, PAGE_SIZE).getSuccess();
        List<AssessmentPeriodResource> newAssessmentPeriodResources
                = range(0, form.numberUnsavedAssessmentPeriods())
                        .mapToObj(AssessmentPeriodResource::new)
                        .collect(toList());
        PageResource<AssessmentPeriodResource> pageResultWithUnsavedAssessmentPeriods
                = actualPageResult.pageResourceWithDummyItemsAddedToLastPage(newAssessmentPeriodResources);
        return view(competitionId, form, pageResultWithUnsavedAssessmentPeriods, model);
    }

    private String view(long competitionId, ManageAssessmentPeriodsForm form, PageResource<AssessmentPeriodResource> pageResult, Model model) {
        model.addAttribute("form", form);
        model.addAttribute("model", assessmentPeriodsPopulator.populateModel(competitionId, pageResult));
        return "competition/manage-assessment-periods";
    }

    private String redirectToManageAssessment(long competitionId) {
        return format("redirect:/assessment/competition/%s", competitionId);
    }

    private String redirectToLastPageToAddAssessmentPeriod(long competitionId) {
        PageResource<AssessmentPeriodResource> page = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId, 0, PAGE_SIZE).getSuccess();
        return format("redirect:/assessment/competition/%s/assessment-period?page=%s&addAssessment=true",
                competitionId,
                page.isLastPageFull() ? page.getTotalPages() + 1: page.getTotalPages());
    }
}