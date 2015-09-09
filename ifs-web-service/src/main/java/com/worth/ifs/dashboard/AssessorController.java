package com.worth.ifs.dashboard;


import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.User;
import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.service.ApplicationService;
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
    ApplicationService applicationService;

    @Autowired
    TokenAuthenticationService tokenAuthenticationService;

    @RequestMapping(value="/dashboard", method= RequestMethod.GET)
    public String dashboard(Model model, HttpServletRequest request) {
        User user = (User)tokenAuthenticationService.getAuthentication(request).getDetails();
        List<Application> applications = applicationService.getApplicationsByUserId(user.getId());
        log.info("Total applications: " + applications.size());

        Map<Long, Integer> applicationProgress = new HashMap<>();
        for (Application application : applications) {
            log.info("State: " + application.getApplicationStatus().getName());

            if(application.getApplicationStatus().getName().equals("created")){
                Double progress = applicationService.getCompleteQuestionsPercentage(application.getId());
                log.info("Application progress : " + progress.intValue());
                applicationProgress.put(application.getId(), progress.intValue());
            }
        }

        ArrayList<Application> inprogress = applications.stream()
                .filter(a -> (a.getApplicationStatus().getName().equals("created") || a.getApplicationStatus().getName().equals("submitted")))
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<Application> finished = applications.stream()
                .filter(a -> (a.getApplicationStatus().getName().equals("approved") || a.getApplicationStatus().getName().equals("rejected")))
                .collect(Collectors.toCollection(ArrayList::new));

        log.debug("inprogress size " + inprogress.size());
        log.debug("finished size " + finished.size());

        model.addAttribute("applicationProgress", applicationProgress);
        model.addAttribute("applicationsInProcess", inprogress);
        model.addAttribute("applicationsFinished", finished);

        return "assessor-dashboard";
    }
}
