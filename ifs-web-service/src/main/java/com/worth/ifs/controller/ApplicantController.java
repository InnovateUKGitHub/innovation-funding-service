package com.worth.ifs.controller;


import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.User;
import com.worth.ifs.filter.LoginFilter;
import com.worth.ifs.service.ApplicationService;
import com.worth.ifs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This controller will handle requests related to the current applicant. So pages that are relative to that user,
 * are implemented here. For example the my-applications page.
 */
@Controller
@RequestMapping("/applicant")
public class ApplicantController {
    @Autowired
    ApplicationService applicationService;
    @Autowired
    UserService userService;

    @RequestMapping(value="/dashboard", method= RequestMethod.GET)
    public String dashboard(Model model, @CookieValue(value = LoginFilter.IFS_AUTH_COOKIE_NAME, defaultValue = "") String token) {
        System.out.println("Show applicant dashboard "+ token);


        User user = userService.retrieveUserByToken(token);
        List<Application> applications = applicationService.getApplicationsByUserId(user.getId());
        System.out.println("Total applications: "+ applications.size());

        for (Application application : applications) {
            System.out.println("State: " +application.getProcessStatus().getName());
        }

        ArrayList<Application> inprogress = applications.stream()
                .filter(a -> (a.getProcessStatus().getName().equals("created") || a.getProcessStatus().getName().equals("submitted")))
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<Application> finished = applications.stream()
                .filter(a -> (a.getProcessStatus().getName().equals("approved") || a.getProcessStatus().getName().equals("rejected")))
                .collect(Collectors.toCollection(ArrayList::new));

        System.out.println("inprogress size " + inprogress.size());
        System.out.println("finished size " + finished.size());


        model.addAttribute("applicationsInProcess", inprogress);
        model.addAttribute("applicationsFinished", finished);

        return "applicant-dashboard";
    }
}
