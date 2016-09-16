package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.model.AssessorDeclarationModelPopulator;
import com.worth.ifs.assessment.model.AssessorSkillsModelPopulator;
import com.worth.ifs.assessment.model.AssessorTermsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to manage Assessor Registration.
 */
@Controller
@RequestMapping("/registration")
public class AssessorRegistrationController {

    @Autowired
    private AssessorSkillsModelPopulator assessorSkillsModelPopulator;

    @Autowired
    private AssessorDeclarationModelPopulator assessorDeclarationModelPopulator;

    @Autowired
    private AssessorTermsModelPopulator assessorTermsModelPopulator;


    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String register() {
        return "registration/register";
    }

    @RequestMapping(value = "skills", method = RequestMethod.GET)
    public String skills(Model model) {
        model.addAttribute("model", assessorSkillsModelPopulator.populateModel());
        return "registration/innovation-areas";
    }

    @RequestMapping(value = "declaration", method = RequestMethod.GET)
    public String declaration(Model model) {
        model.addAttribute("model", assessorDeclarationModelPopulator.populateModel());
        return "registration/declaration-of-interest";
    }

    @RequestMapping(value = "terms", method = RequestMethod.GET)
    public String terms(Model model) {
        model.addAttribute("model", assessorTermsModelPopulator.populateModel());
        return "registration/terms";
    }
}