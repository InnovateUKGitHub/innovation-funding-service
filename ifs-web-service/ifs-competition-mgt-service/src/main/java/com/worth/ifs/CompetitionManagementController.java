package com.worth.ifs;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/competition")
public class CompetitionManagementController {
    private static final Log LOG = LogFactory.getLog(CompetitionManagementController.class);
    
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
			default:
				return "redirect:/login";
    	}
    }

	private String openCompetition(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm,
			BindingResult bindingResult) {

		String sort = applicationSummarySortFieldService.sortFieldForOpenCompetition(queryForm.getSort());

		ApplicationSummaryPageResource applicationSummary = applicationSummaryService.findByCompetitionId(competitionId, queryForm.getPage() - 1, sort);
		model.addAttribute("results", applicationSummary);
		model.addAttribute("activeSortField", sort);
		model.addAttribute("activeTab", "allApplications");

        return "comp-mgt-open";
	}

	private String inAssessmentCompetition(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm,
			BindingResult bindingResult) {
		
		if("notSubmitted".equals(queryForm.getTab())) {
			populateNotSubmittedModel(model, competitionId, queryForm, bindingResult);
		} else {
			populateSubmittedModel(model, competitionId, queryForm, bindingResult);
		}
		
        return "comp-mgt-in-assessment";
	}
	
	private String fundersPanelCompetition(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm,
			BindingResult bindingResult) {
		
		if("notSubmitted".equals(queryForm.getTab())) {
			populateNotSubmittedModel(model, competitionId, queryForm, bindingResult);
		} else {
			populateSubmittedModel(model, competitionId, queryForm, bindingResult);
		}
		
		return "comp-mgt-funders-panel";
	}

	private void populateNotSubmittedModel(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm,
			BindingResult bindingResult) {
		String sort = applicationSummarySortFieldService.sortFieldForNotSubmittedApplications(queryForm.getSort());
		ApplicationSummaryPageResource results = applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(competitionId, queryForm.getPage() - 1, sort);
		model.addAttribute("results", results);
		model.addAttribute("activeTab", "notSubmitted");
		model.addAttribute("activeSortField", sort);
	}

	private void populateSubmittedModel(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm,
			BindingResult bindingResult) {
		String sort = applicationSummarySortFieldService.sortFieldForSubmittedApplications(queryForm.getSort());
		ApplicationSummaryPageResource results = applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(competitionId, queryForm.getPage() - 1, sort);
		model.addAttribute("results", results);
		model.addAttribute("activeTab", "submitted");
		model.addAttribute("activeSortField", sort);
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
