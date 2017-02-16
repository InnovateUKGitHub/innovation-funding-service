package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.application.service.ApplicationSummaryService;
import org.innovateuk.ifs.application.service.AssessorFeedbackService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.form.ApplicationSummaryQueryForm;
import org.innovateuk.ifs.competition.form.FundingDecisionForm;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.ApplicationSummarySortFieldService;
import org.innovateuk.ifs.management.controller.CompetitionManagementApplicationController.ApplicationOverviewOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

/**
 * Handles the Competition Management Funding decision views and submission of funding decision.
 */
@Controller
@RequestMapping("/competition/{competitionId}/funding")
public class CompetitionManagementFundingController {

    private static final int PAGE_SIZE = 1;

    @Autowired
    private ApplicationSummarySortFieldService applicationSummarySortFieldService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private AssessorFeedbackService assessorFeedbackService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationFundingDecisionService applicationFundingDecisionService;

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String applications(Model model,
                               @PathVariable("competitionId") Long competitionId,
                               @RequestParam MultiValueMap<String, String> queryParams,
                               @ModelAttribute @Valid ApplicationSummaryQueryForm queryForm,
                               @ModelAttribute FundingDecisionForm fundingDecisionForm,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/competition/" + competitionId + "/funding";
        }

        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId);

        if(fundingDecisionForm.getFundingDecision() != null) {
            validator.validate(fundingDecisionForm, bindingResult);
            if(!bindingResult.hasErrors()) {
                applicationFundingDecisionService.saveApplicationFundingDecisionData(competitionId, fundingDecisionForm.getFundingDecision(), fundingDecisionForm.getApplicationIds());
            }
        }

        model.addAttribute("competitionSummary", competitionSummary);

        model.addAttribute("originQuery", buildOriginQueryString(ApplicationOverviewOrigin.FUNDING_APPLICATIONS, queryParams));

        switch (competitionSummary.getCompetitionStatus()) {
            case FUNDERS_PANEL:
                return fundersPanelCompetition(model, competitionSummary, queryForm, bindingResult);
            case ASSESSOR_FEEDBACK:
                return assessorFeedbackCompetition(model, competitionSummary, queryForm, bindingResult);
            default:
                return "redirect:/login";
        }
    }

    private String fundersPanelCompetition(Model model, CompetitionSummaryResource competitionSummary, ApplicationSummaryQueryForm queryForm, BindingResult bindingResult) {
        if ("notSubmitted".equals(queryForm.getTab())) {
            populateNotSubmittedModel(model, competitionSummary, queryForm);
        } else {
            populateSubmittedModel(model, competitionSummary, queryForm, Integer.MAX_VALUE);
        }
        return "comp-mgt-funders-panel";
    }

    private String assessorFeedbackCompetition(Model model, CompetitionSummaryResource competitionSummary, ApplicationSummaryQueryForm queryForm, BindingResult bindingResult) {
        populateModelBasedOnAssessorTabState(model, competitionSummary, queryForm, bindingResult);
        return "comp-mgt-assessor-feedback";
    }

    private void populateModelBasedOnAssessorTabState(Model model, CompetitionSummaryResource competitionSummary, ApplicationSummaryQueryForm queryForm, BindingResult bindingResult) {
        if ("overview".equals(queryForm.getTab())) {
            populateOverviewModel(model, competitionSummary);
        } else if ("notSubmitted".equals(queryForm.getTab())) {
            populateNotSubmittedModel(model, competitionSummary, queryForm);
        } else {
            boolean canPublishAssessorFeedback = assessorFeedbackService.feedbackUploaded(competitionSummary.getCompetitionId());
            model.addAttribute("canPublishAssessorFeedback", canPublishAssessorFeedback);
            populateSubmittedModel(model, competitionSummary, queryForm, PAGE_SIZE);
        }
    }

    private void populateNotSubmittedModel(Model model, CompetitionSummaryResource competitionSummary, ApplicationSummaryQueryForm queryForm) {
        String sort = applicationSummarySortFieldService.sortFieldForNotSubmittedApplications(queryForm.getSort());
        ApplicationSummaryPageResource results = applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(competitionSummary.getCompetitionId(), sort, queryForm.getPage() - 1, PAGE_SIZE);
        model.addAttribute("results", results);
        model.addAttribute("activeTab", "notSubmitted");
        model.addAttribute("activeSortField", sort);
    }

    private void populateOverviewModel(Model model, CompetitionSummaryResource competitionSummary) {
        model.addAttribute("applicationsRequiringFeedback", applicationSummaryService.getApplicationsRequiringFeedbackCountByCompetitionId(competitionSummary.getCompetitionId()));

        CompetitionResource competition = competitionService.getById(competitionSummary.getCompetitionId());

        model.addAttribute("assessmentEndDate", competition.getFundersPanelDate());
        model.addAttribute("assessmentDaysLeft", competition.getAssessmentDaysLeft());
        model.addAttribute("assessmentDaysLeftPercentage", competition.getAssessmentDaysLeftPercentage());

        model.addAttribute("activeTab", "overview");
    }

    private void populateSubmittedModel(Model model, CompetitionSummaryResource competitionSummary, ApplicationSummaryQueryForm queryForm, Integer pageSize) {
        String sort = applicationSummarySortFieldService.sortFieldForSubmittedApplications(queryForm.getSort());
        ApplicationSummaryPageResource results = applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(competitionSummary.getCompetitionId(), sort, queryForm.getPage() - 1, 1);
        model.addAttribute("results", results);
        model.addAttribute("activeTab", "submitted");
        model.addAttribute("activeSortField", sort);
    }
}
