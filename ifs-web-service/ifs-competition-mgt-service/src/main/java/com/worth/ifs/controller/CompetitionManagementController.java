package com.worth.ifs.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.controller.form.ApplicationSummaryQueryForm;
import com.worth.ifs.service.ApplicationSummarySortFieldService;

@Controller
@RequestMapping("/competition")
public class CompetitionManagementController {
    
	private static final int PAGE_SIZE = 20;
	
    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private ApplicationSummarySortFieldService applicationSummarySortFieldService;
    
    @RequestMapping("/{competitionId}")
    public String displayCompetitionInfo(Model model, @PathVariable("competitionId") Long competitionId, @ModelAttribute @Valid ApplicationSummaryQueryForm queryForm, BindingResult bindingResult){

    	if(bindingResult.hasErrors()) {
    		return "redirect:/competition/" + competitionId;
    	}
    	
    	CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId);
    	
    	model.addAttribute("competitionSummary", competitionSummary);
    	 
    	switch(competitionSummary.getCompetitionStatus()) {
    		case NOT_STARTED:
    			return "comp-mgt-not-started";
	    	case OPEN:
	    		return openCompetition(model, competitionId, queryForm, bindingResult);
	    	case IN_ASSESSMENT:
	    		return inAssessmentCompetition(model, competitionId, queryForm, bindingResult);
	    	case FUNDERS_PANEL:
	    		return fundersPanelCompetition(model, competitionId, queryForm, bindingResult);
	    	case ASSESSOR_FEEDBACK:
	    		return assessorFeedbackCompetition(model, competitionId, queryForm, bindingResult);
	    	case PROJECT_SETUP:
	    		return "comp-mgt-project-setup";
			default:
				return "redirect:/login";
    	}
    }

	private String openCompetition(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm, BindingResult bindingResult) {

		String sort = applicationSummarySortFieldService.sortFieldForOpenCompetition(queryForm.getSort());

		ApplicationSummaryPageResource applicationSummary = applicationSummaryService.findByCompetitionId(competitionId, sort, queryForm.getPage() - 1, PAGE_SIZE);
		model.addAttribute("results", applicationSummary);
		model.addAttribute("activeSortField", sort);
		model.addAttribute("activeTab", "allApplications");

        return "comp-mgt-open";
	}

	private String inAssessmentCompetition(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm, BindingResult bindingResult) {
		if("notSubmitted".equals(queryForm.getTab())) {
			populateNotSubmittedModel(model, competitionId, queryForm);
		} else {
			populateSubmittedModel(model, competitionId, queryForm, PAGE_SIZE);
		}
        return "comp-mgt-in-assessment";
	}
	
	private String fundersPanelCompetition(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm, BindingResult bindingResult) {
		if("notSubmitted".equals(queryForm.getTab())) {
			populateNotSubmittedModel(model, competitionId, queryForm);
		} else {
			queryForm.setPage(1);
			populateSubmittedModel(model, competitionId, queryForm, Integer.MAX_VALUE);
		}
		return "comp-mgt-funders-panel";
	}
	
	private String assessorFeedbackCompetition(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm, BindingResult bindingResult) {
		populateModelBasedOnAssessorTabState(model, competitionId, queryForm, bindingResult);
		return "comp-mgt-assessor-feedback";
	}

	private void populateModelBasedOnAssessorTabState(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm, BindingResult bindingResult) {
		if("overview".equals(queryForm.getTab())) {
			populateOverviewModel(model, competitionId);
		} else if("notSubmitted".equals(queryForm.getTab())) {
			populateNotSubmittedModel(model, competitionId, queryForm);
		} else {
			populateSubmittedModel(model, competitionId, queryForm, PAGE_SIZE);
		}
	}

	private void populateNotSubmittedModel(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm) {
		String sort = applicationSummarySortFieldService.sortFieldForNotSubmittedApplications(queryForm.getSort());
		ApplicationSummaryPageResource results = applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(competitionId, sort, queryForm.getPage() - 1, PAGE_SIZE);
		model.addAttribute("results", results);
		model.addAttribute("activeTab", "notSubmitted");
		model.addAttribute("activeSortField", sort);
	}

	private void populateSubmittedModel(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm, Integer pageSize) {
		String sort = applicationSummarySortFieldService.sortFieldForSubmittedApplications(queryForm.getSort());
		ApplicationSummaryPageResource results = applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(competitionId, sort, queryForm.getPage() - 1, pageSize);
		model.addAttribute("results", results);
		model.addAttribute("activeTab", "submitted");
		model.addAttribute("activeSortField", sort);
	}
	
	private void populateOverviewModel(Model model, Long competitionId) {
		model.addAttribute("applicationsRequiringFeedback", applicationSummaryService.getApplicationsRequiringFeedbackCountByCompetitionId(competitionId));
		model.addAttribute("projectsBeingSetUp", applicationSummaryService.getFundedApplicationCountByCompetitionId(competitionId));
		model.addAttribute("activeTab", "overview");
	}

	@RequestMapping("/{competitionId}/download")
    public void downloadApplications(@PathVariable("competitionId") Long competitionId, HttpServletResponse response) throws IOException {
        CompetitionResource competition = competitionService.getById(competitionId);
        if(competition!= null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
            String filename = String.format("Submitted_Applications_Competition_%s_%s_%s.xlsx", competitionId, competition.getName(), LocalDateTime.now().format(formatter));
            response.setContentType("application/force-download");
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Content-Disposition", "attachment; filename=\""+filename+"\"");
            final ByteArrayResource resource = applicationSummaryService.downloadByCompetition(competitionId);

            IOUtils.copy(resource.getInputStream(), response.getOutputStream());
            response.flushBuffer();
        }
    }
}
