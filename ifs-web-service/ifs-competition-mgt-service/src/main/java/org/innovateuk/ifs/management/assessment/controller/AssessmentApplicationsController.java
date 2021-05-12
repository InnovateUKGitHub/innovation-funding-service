package org.innovateuk.ifs.management.assessment.controller;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.service.ApplicationCountSummaryRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.management.application.list.populator.ManageApplicationsModelPopulator;
import org.innovateuk.ifs.management.assessment.populator.AssessmentPeriodChoiceModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static java.lang.String.format;

@Controller
@RequestMapping("/assessment/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can manage assessment applications", securedType = AssessmentApplicationsController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSMENT_APPLICATIONS')")
public class AssessmentApplicationsController extends BaseAssessmentController {

    @Autowired
    private ApplicationCountSummaryRestService applicationCountSummaryRestService;

    @Autowired
    private ManageApplicationsModelPopulator manageApplicationsPopulator;

    @Autowired
    private AssessmentPeriodRestService assessmentPeriodRestService;

    @Autowired
    private AssessmentPeriodChoiceModelPopulator assessmentPeriodChoiceModelPopulator;

    @GetMapping("/applications/period")
    public String manageApplicationsForPeriod(Model model,
                                              @RequestParam(value = "assessmentPeriodId", required = false) Long assessmentPeriodId,
                                              @PathVariable("competitionId") long competitionId,
                                              @RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "filterSearch", defaultValue = "") String filter) {
        if (assessmentPeriodId == null) {
            return format("redirect:/assessment/competition/%s/appliications", competitionId);
        }
        CompetitionResource competitionResource = getCompetition(competitionId);

        ApplicationCountSummaryPageResource applicationCounts = getCounts(competitionId, assessmentPeriodId, page, filter);

        model.addAttribute("model", manageApplicationsPopulator.populateModel(competitionResource, applicationCounts, filter, assessmentPeriodId));

        return "competition/manage-applications";
    }

    @GetMapping("/applications")
    public String manageApplications(Model model,
                                     @PathVariable("competitionId") long competitionId,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "filterSearch", required = false) String filter) {
        List<AssessmentPeriodResource> assessmentPeriods = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId).getSuccess();
        if (assessmentPeriods.size() > 1) {
            model.addAttribute("model", assessmentPeriodChoiceModelPopulator.populate(competitionId));
            return "competition/application-progress-choose-period";
        }
        AssessmentPeriodResource assessmentPeriod = assessmentPeriods.get(0);
        return format("redirect:/assessment/competition/%s/applications/period?assessmentPeriodId=%s", competitionId, assessmentPeriod.getId());
    }

    private ApplicationCountSummaryPageResource getCounts(long competitionId, long assessmentPeriodId, int page, String filter) {
        return applicationCountSummaryRestService
                .getApplicationCountSummariesByCompetitionIdAndAssessmentPeriodId(competitionId, assessmentPeriodId, page, PAGE_SIZE, filter)
                .getSuccess();
    }
}
