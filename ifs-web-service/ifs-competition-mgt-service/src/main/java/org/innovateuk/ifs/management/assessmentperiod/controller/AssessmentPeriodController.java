package org.innovateuk.ifs.management.assessmentperiod.controller;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.commons.resource.PageResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.assessmentperiod.form.ManageAssessmentPeriodsForm;
import org.innovateuk.ifs.management.assessmentperiod.populator.AssessmentPeriodFormPopulator;
import org.innovateuk.ifs.management.assessmentperiod.populator.ManageAssessmentPeriodsPopulator;
import org.innovateuk.ifs.management.assessmentperiod.saver.AssessmentPeriodSaver;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.Map.Entry;
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
        PageResource<AssessmentPeriodResource> pageResult = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId, page - 1, PAGE_SIZE).getSuccess();
        model.addAttribute("form", formPopulator.populate(competitionId, pageResult, addAssessment));
        return view(competitionId, pageResult, model);
    }

    private String view(long competitionId, PageResource<AssessmentPeriodResource> pageResult, Model model) {
        model.addAttribute("model", assessmentPeriodsPopulator.populateModel(competitionId, pageResult)); // TODO
        return "competition/manage-assessment-periods";
    }

    private String view(long competitionId, int page, Model model) {
        PageResource<AssessmentPeriodResource> pageResult = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId, page, PAGE_SIZE).getSuccess();
        return view(competitionId, pageResult, model);
    }

    @PostMapping
    public String submitAssessmentPeriods(@Valid @ModelAttribute(value = "form") ManageAssessmentPeriodsForm form,
                                          @SuppressWarnings("unused") BindingResult bindingResult,
                                          ValidationHandler validationHandler,
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          Model model,
                                          @PathVariable long competitionId) {
        Supplier<String> successView = () -> redirectToManageAssessment(competitionId);
        return submit(form, validationHandler, page, model, competitionId, successView);
    }

    private String submit(ManageAssessmentPeriodsForm form,
                          ValidationHandler validationHandler,
                          int page,
                          Model model,
                          long competitionId,
                          Supplier<String> successView) {
        Supplier<String> failureView = () -> {
            form.getAssessmentPeriods().forEach(p -> p.setMilestoneEntries(
                    p.getMilestoneEntries()
                            .entrySet()
                            .stream()
                            .sorted(Comparator.comparing(entry -> MilestoneType.valueOf(entry.getKey()).ordinal()))
                            .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedMap::new))
            ));
            return view(competitionId, page, model);
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> saveResult = saver.save(competitionId, form);
            return  validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, successView);
        });
    }


    @PostMapping(params = "add-assessment-period")
    public String addAssessmentPeriod(@ModelAttribute(value = "form") ManageAssessmentPeriodsForm form,
                                      @SuppressWarnings("unused") BindingResult bindingResult,
                                      ValidationHandler validationHandler,
                                      @RequestParam(value = "page", defaultValue = "1") int page,
                                      Model model,
                                      @PathVariable long competitionId) {
        // TODO remove
        saver.createNewAssessmentPeriod(competitionId);
        Supplier<String> successView = () -> redirectToLastPageToAddAssessmentPeriod(competitionId);
        return submit(form, validationHandler, page, model, competitionId, successView);
    }

    private String redirectToManageAssessment(long competitionId) {
        return format("redirect:/assessment/competition/%s", competitionId);
    }

    private String redirectToLastPageToAddAssessmentPeriod(long competitionId) {
        PageResource<AssessmentPeriodResource> page = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId, 1, PAGE_SIZE).getSuccess();
        return format("redirect:/assessment/competition/%s/assessment-period?page=%s&addAssessment=true",
                competitionId,
                page.lastPageFull() ? page.getTotalPages() + 1: page.getTotalPages());
    }

}
