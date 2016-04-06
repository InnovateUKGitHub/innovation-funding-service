package com.worth.ifs;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionApplicationSummaryPageResource;
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
import java.io.IOException;
import java.time.LocalDateTime;

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
    	
    	CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId);
    	
    	model.addAttribute("competitionSummary", competitionSummary);
    	 
    	switch(competitionSummary.getCompetitionStatus()) {
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
		
		ClosedCompetitionApplicationSummaryPageResource results;
		if("notSubmitted".equals(queryForm.getTab())) {
			results = applicationSummaryService.getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(competitionId, queryForm.getPage() - 1, queryForm.getSort());
			model.addAttribute("activeTab", "notSubmitted");
		} else {
			results = applicationSummaryService.getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(competitionId, queryForm.getPage() - 1, queryForm.getSort());
			model.addAttribute("activeTab", "submitted");
		}
		
		model.addAttribute("results", results);
		
        LOG.warn("Show in assessment competition info");
        return "comp-mgt-in-assessment";
	}


	private String openCompetition(Model model, Long competitionId, ApplicationSummaryQueryForm queryForm,
			BindingResult bindingResult) {
		
		ApplicationSummaryPageResource applicationSummary = applicationSummaryService.findByCompetitionId(competitionId, queryForm.getPage() - 1, queryForm.getSort());
		model.addAttribute("results", applicationSummary);

        LOG.warn("Show open competition info");
        return "comp-mgt";
	}



    @RequestMapping("/{competitionId}/download")
    public void downloadApplications(@PathVariable("competitionId") Long competitionId, HttpServletResponse response) throws IOException {
        CompetitionResource competition = competitionService.getById(competitionId);
        if(competition!= null){
            String filename = String.format("Submitted_Applications_Competition_%s_%s_%s.xls", competitionId, competition.getName(), LocalDateTime.now());
            response.setContentType("application/force-download");
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Content-Disposition", "attachment; filename=\""+filename+"\"");
            final ByteArrayResource resource = applicationSummaryService.downloadByCompetition(competitionId);

            IOUtils.copy(resource.getInputStream(), response.getOutputStream());
            response.flushBuffer();
        }

    }
}
