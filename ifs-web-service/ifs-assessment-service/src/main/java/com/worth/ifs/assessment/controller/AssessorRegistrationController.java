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

}