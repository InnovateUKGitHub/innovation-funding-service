package com.worth.ifs;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.assessment.service.AssessmentRestService;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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


    private User getLoggedUser(HttpServletRequest req) {
        return userAuthenticationService.getAuthenticatedUser(req);
    }

    @RequestMapping("/competition/{competitionId}")
    public String displayCompetitionInfo(){
        LOG.warn("Show competition info ");
        return "test-comp-mgt";
    }
}
