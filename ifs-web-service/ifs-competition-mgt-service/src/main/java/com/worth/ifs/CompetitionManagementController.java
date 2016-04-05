package com.worth.ifs;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.service.ApplicationSummaryRestService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.assessment.service.AssessmentRestService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.domain.User;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/competition")
public class CompetitionManagementController {
    private static final Log LOG = LogFactory.getLog(CompetitionManagementController.class);
    @Autowired
    CompetitionService competitionService;

    @Autowired
    AssessmentRestService assessmentRestService;

    @Autowired
    UserAuthenticationService userAuthenticationService;
    
    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;


    private User getLoggedUser(HttpServletRequest req) {
        return userAuthenticationService.getAuthenticatedUser(req);
    }

    @RequestMapping("/{competitionId}")
    public String displayCompetitionInfo(Model model, @PathVariable("competitionId") Long competitionId, @ModelAttribute ApplicationSummaryQueryForm queryForm, BindingResult bindingResult){
    	
    	if(bindingResult.hasErrors()) {
    		return "redirect:/management/competition/1";
    	}
    	
    	RestResult<ApplicationSummaryPageResource> restResult = applicationSummaryRestService.findByCompetitionId(competitionId, queryForm.getPage() - 1, queryForm.getSort());
    	
    	if(restResult.isSuccess()) {
    		model.addAttribute("results", restResult.getSuccessObject());
    	} else {
            return "redirect:/login";
    	}
    	
    	model.addAttribute("competitionId", competitionId);
    	
        LOG.warn("Show competition info ");
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
            final ByteArrayResource resource = applicationSummaryRestService.downloadByCompetition(competitionId).getSuccessObject();

            IOUtils.copy(resource.getInputStream(), response.getOutputStream());
            response.flushBuffer();
        }

    }
}
