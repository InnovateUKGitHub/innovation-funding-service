package com.worth.ifs.controller;

import com.worth.ifs.filter.LoginFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/applicant")
public class ApplicantController {

    @RequestMapping(value="/dashboard", method= RequestMethod.GET)
    public String dashboard(Model model, @CookieValue(value = LoginFilter.IFS_AUTH_COOKIE_NAME, defaultValue = "") String token) {
        System.out.println("Show applicant dashboard "+ token);
        return "applicant-dashboard";
    }
}
