package com.worth.ifs.assessment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to manage Assessor Registration.
 */
@Controller
@RequestMapping("/registration")
public class AssessorRegistrationController {

    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String register() {
        return "registration/register";
    }

    @RequestMapping(value = "skills", method = RequestMethod.GET)
    public String skills() {
        return "registration/innovation-areas";
    }

    @RequestMapping(value = "declaration", method = RequestMethod.GET)
    public String declaration() {
        return "registration/declaration-of-interest";
    }

    @RequestMapping(value = "terms", method = RequestMethod.GET)
    public String terms() {
        return "registration/terms";
    }
}