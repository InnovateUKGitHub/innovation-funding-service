package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.form.AssessorDeclarationForm;
import com.worth.ifs.assessment.form.AssessorSkillsForm;
import com.worth.ifs.assessment.form.AssessorTermsForm;
import com.worth.ifs.assessment.model.AssessorDeclarationModelPopulator;
import com.worth.ifs.assessment.model.AssessorSkillsModelPopulator;
import com.worth.ifs.assessment.model.AssessorTermsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to manage Assessor Registration.
 */
@Controller
@RequestMapping("/registration")
public class AssessorRegistrationController {

    private static final String FORM_ATTR_NAME = "form";

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
    public String getSkills(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorSkillsForm form) {
        model.addAttribute("model", assessorSkillsModelPopulator.populateModel());
        return "registration/innovation-areas";
    }

    @RequestMapping(value = "skills", method = RequestMethod.POST)
    public String submitSkills(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorSkillsForm form) {
        return "redirect:/registration/declaration";
    }

    @RequestMapping(value = "declaration", method = RequestMethod.GET)
    public String getDeclaration(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorDeclarationForm form) {
        model.addAttribute("model", assessorDeclarationModelPopulator.populateModel());
        return "registration/declaration-of-interest";
    }

    @RequestMapping(value = "declaration", method = RequestMethod.POST)
    public String submitDeclaration(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorDeclarationForm form) {
        return "redirect:/registration/terms";
    }

    @RequestMapping(value = "terms", method = RequestMethod.GET)
    public String getTerms(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorTermsForm form) {
        model.addAttribute("model", assessorTermsModelPopulator.populateModel());
        return "registration/terms";
    }

    @RequestMapping(value = "terms", method = RequestMethod.POST)
    public String submitTerms(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorTermsForm form) {
        return "registration/terms";
    }

}