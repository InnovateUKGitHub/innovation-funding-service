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

import java.util.List;


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


//        applications = new ArrayList<>();
//        Application app = new Application();
//        app.setName("Rovel Additive Manufacturing Process123");
//        applications.add(app);

        model.addAttribute("applicationsInProcess", applications);

        return "applicant-dashboard";
    }
}
