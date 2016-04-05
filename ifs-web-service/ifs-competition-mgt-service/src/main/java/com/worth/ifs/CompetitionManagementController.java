package com.worth.ifs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;

@Controller
@RequestMapping("/competition")
public class CompetitionManagementController {
    private static final Log LOG = LogFactory.getLog(CompetitionManagementController.class);
    
    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;
    

    @RequestMapping("/{competitionId}")
    public String displayCompetitionInfo(Model model, @PathVariable("competitionId") Long competitionId, @ModelAttribute ApplicationSummaryQueryForm queryForm, BindingResult bindingResult){
    	
    	if(bindingResult.hasErrors()) {
    		return "redirect:/management/competition/1";
    	}
    	
    	CompetitionResource competition = competitionService.getById(competitionId);
    	
    	switch(competition.getCompetitionStatus()) {
	    	case OPEN:
	    		return openCompetition(model, competitionId, queryForm, bindingResult);
	    	case IN_ASSESSMENT:
	    		return inAssessmentCompetition(model, competitionId, queryForm, bindingResult);
			default:
				return "redirect:/login";
    	}
    }

	private String inAssessmentCompetition(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm,
			BindingResult bindingResult) {
		ClosedCompetitionApplicationSummaryPageResource notSubmittedApplicationSummary = applicationSummaryService.getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(competitionId, queryForm.getPage() - 1, queryForm.getSort());
		ClosedCompetitionApplicationSummaryPageResource submittedApplicationSummary = applicationSummaryService.getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(competitionId, queryForm.getPage() - 1, queryForm.getSort());
		
		model.addAttribute("notSubmittedResults", notSubmittedApplicationSummary);
		model.addAttribute("submittedResults", submittedApplicationSummary);
		
		CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId);
        model.addAttribute("competitionSummary", competitionSummary);
    	
        model.addAttribute("currentCompetition", competitionService.getById(competitionId));
    	
        LOG.warn("Show in assessment competition info");
        return "comp-mgt-in-assessment";
	}


	private String openCompetition(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm,
			BindingResult bindingResult) {
		
		ApplicationSummaryPageResource applicationSummary = applicationSummaryService.findByCompetitionId(competitionId, queryForm.getPage() - 1, queryForm.getSort());
		model.addAttribute("results", applicationSummary);

        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId);
        model.addAttribute("competitionSummary", competitionSummary);

        model.addAttribute("currentCompetition", competitionService.getById(competitionId));

        LOG.warn("Show open competition info ");
        return "comp-mgt";
	}
}
