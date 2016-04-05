package com.worth.ifs;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationSummaryRestService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.assessment.service.AssessmentRestService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

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

        RestResult<CompetitionSummaryResource> competitionSummaryResourceRestResult = applicationSummaryRestService.getCompetitionSummaryByCompetitionId(competitionId);
        if(competitionSummaryResourceRestResult.isSuccess()){
            model.addAttribute("competitionSummary", competitionSummaryResourceRestResult.getSuccessObject());
        }

        model.addAttribute("currentCompetition", competitionService.getById(competitionId));

        LOG.warn("Show competition info ");
        return "comp-mgt";
    }
}