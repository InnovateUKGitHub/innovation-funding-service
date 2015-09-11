package com.worth.ifs.dashboard;


import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Competition;
import com.worth.ifs.domain.User;
import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.service.ApplicationService;
import com.worth.ifs.service.CompetitionsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This controller will handle requests related to the current applicant. So pages that are relative to that user,
 * are implemented here. For example the my-applications page.
 */
@Controller
@RequestMapping("/assessor")
public class AssessorController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    CompetitionsService competitionsService;

    @Autowired
    TokenAuthenticationService tokenAuthenticationService;

    @RequestMapping(value="/dashboard", method= RequestMethod.GET)
    public String dashboard(Model model, HttpServletRequest request) {
        User user = (User)tokenAuthenticationService.getAuthentication(request).getDetails();
        List<Competition> competitions = competitionsService.getAll();




        System.out.println("Competition names ");
        for ( Competition c : competitions) {
            //c.getAssessmentDaysLeft()
            System.out.println("Competition with name: " + c.getName() + " assessmentStart: " + c.getAssessmentStartDate() + " assessmentEnd: " + c.getAssessmentEndDate());
        }

        model.addAttribute("applicationProgress", null);
        model.addAttribute("applicationsInProcess", null);
        model.addAttribute("applicationsFinished", null);

        model.addAttribute("competitionsForAssessment", competitions);


        return "assessor-dashboard";
    }
}
