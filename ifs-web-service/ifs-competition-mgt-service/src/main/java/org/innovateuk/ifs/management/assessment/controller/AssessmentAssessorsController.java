package org.innovateuk.ifs.management.assessment.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.service.AssessorCountSummaryRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.assessment.form.AssessmentPeriodForm;
import org.innovateuk.ifs.management.assessment.populator.AssessmentPeriodChoiceModelPopulator;
import org.innovateuk.ifs.management.assessor.populator.ManageAssessorsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;

@Controller
@RequestMapping("/assessment/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can manage assessments", securedType = AssessmentAssessorsController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSMENT')")
public class AssessmentAssessorsController extends BaseAssessmentController {

    private static final String FILTER_FORM_ATTR_NAME = "filterForm";
    @Autowired
    private AssessorCountSummaryRestService applicationCountSummaryRestService;

    @Autowired
    private ManageAssessorsModelPopulator manageApplicationsPopulator;

    @Autowired
    private AssessmentPeriodRestService assessmentPeriodRestService;

    @Autowired
    private AssessmentPeriodChoiceModelPopulator assessmentPeriodChoiceModelPopulator;

    @GetMapping("/assessors")
    public String manageAssessors(Model model, @PathVariable("competitionId") long competitionId) {
        List<AssessmentPeriodResource> assessmentPeriods = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId).getSuccess();

        if (assessmentPeriods.size() > 1) {
            return assessorProgressChoosePeriodView(model, new AssessmentPeriodForm(), competitionId).get();
        }
        AssessmentPeriodResource assessmentPeriod = assessmentPeriods.get(0);

        return format("redirect:/assessment/competition/%s/assessors/period?assessmentPeriodId=%s", competitionId, assessmentPeriod.getId());
    }

    @PostMapping("/assessors")
    public String manageAssessors(Model model,
                                  @Valid @ModelAttribute("form") AssessmentPeriodForm assessmentPeriodForm,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  @PathVariable("competitionId") long competitionId) {
        return validationHandler.failNowOrSucceedWith(
                assessorProgressChoosePeriodView(model, assessmentPeriodForm, competitionId),
                () -> format("redirect:/assessment/competition/%s/assessors/period?assessmentPeriodId=%s", competitionId, assessmentPeriodForm.getAssessmentPeriodId()));
    }

    private Supplier<String> assessorProgressChoosePeriodView(Model model, AssessmentPeriodForm assessmentPeriodForm, long competitionId){
        return () -> {
            model.addAttribute("model", assessmentPeriodChoiceModelPopulator.populate(competitionId));
            model.addAttribute("form", assessmentPeriodForm);
            return "competition/assessor-progress-choose-period";
        };
    }

    @GetMapping("/assessors/period")
    public String manageAssessorsForPeriod(Model model,
                                           @PathVariable("competitionId") long competitionId,
                                           @RequestParam(value = "assessmentPeriodId", required = false) Long assessmentPeriodId,
                                           @RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "assessorNameFilter", required = false) String assessorNameFilter) {
        if (assessmentPeriodId == null) {
            return format("redirect:/assessment/competition/%s/assessors", competitionId);
        }
        CompetitionResource competitionResource = getCompetition(competitionId);
        AssessorCountSummaryPageResource applicationCounts = getCounts(competitionId, assessmentPeriodId, assessorNameFilter, page);

        model.addAttribute("model", manageApplicationsPopulator.populateModel(competitionResource, applicationCounts, assessmentPeriodId));

        return "competition/manage-assessors";
    }

    private AssessorCountSummaryPageResource getCounts(long competitionId, long assessmentPeriodId, String assessorNameFilter, int page) {
        return applicationCountSummaryRestService
                .getAssessorCountSummariesByCompetitionIdAndAssessmentPeriodId(competitionId, assessmentPeriodId, StringUtils.trim(assessorNameFilter), page, PAGE_SIZE)
                .getSuccess();
    }
}