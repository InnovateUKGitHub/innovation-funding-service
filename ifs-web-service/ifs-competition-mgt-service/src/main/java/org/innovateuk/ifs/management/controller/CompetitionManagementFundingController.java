package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryService;
import org.innovateuk.ifs.competition.form.ApplicationSummaryQueryForm;
import org.innovateuk.ifs.competition.service.ApplicationSummarySortFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping("/competition/{competitionId}/funding")
public class CompetitionManagementFundingController {

    private static final int PAGE_SIZE = 20;

    @Autowired
    private ApplicationSummarySortFieldService applicationSummarySortFieldService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @RequestMapping(method = RequestMethod.GET)
    public String applications(Model model,
                               @PathVariable("competitionId") Long competitionId,
                               @ModelAttribute @Valid ApplicationSummaryQueryForm queryForm,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/competition/" + competitionId + "/funding";
        }

        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId);
        model.addAttribute("competitionSummary", competitionSummary);
        if("notSubmitted".equals(queryForm.getTab())) {
            populateNotSubmittedModel(model, competitionSummary, queryForm);
        } else {
            queryForm.setPage(1);
            populateSubmittedModel(model, competitionSummary, queryForm, Integer.MAX_VALUE);
        }
        return "comp-mgt-funders-panel";
    }

    private void populateNotSubmittedModel(Model model, CompetitionSummaryResource competitionSummary, ApplicationSummaryQueryForm queryForm) {
        String sort = applicationSummarySortFieldService.sortFieldForNotSubmittedApplications(queryForm.getSort());
        ApplicationSummaryPageResource results = applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(competitionSummary.getCompetitionId(), sort, queryForm.getPage() - 1, PAGE_SIZE);
        model.addAttribute("results", results);
        model.addAttribute("activeTab", "notSubmitted");
        model.addAttribute("activeSortField", sort);
    }

    private void populateSubmittedModel(Model model, CompetitionSummaryResource competitionSummary, ApplicationSummaryQueryForm queryForm, Integer pageSize) {
        String sort = applicationSummarySortFieldService.sortFieldForSubmittedApplications(queryForm.getSort());
        ApplicationSummaryPageResource results = applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(competitionSummary.getCompetitionId(), sort, queryForm.getPage() - 1, pageSize);
        model.addAttribute("results", results);
        model.addAttribute("activeTab", "submitted");
        model.addAttribute("activeSortField", sort);
    }
}
