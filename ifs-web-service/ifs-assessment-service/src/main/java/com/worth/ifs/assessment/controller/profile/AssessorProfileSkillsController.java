package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.assessment.form.AssessorRegistrationSkillsForm;
import com.worth.ifs.assessment.model.AssessorRegistrationSkillsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to manage the Assessor Profile Skills page
 */
@Controller
@RequestMapping("/profile/skills")
public class AssessorProfileSkillsController {

    @Autowired
    private AssessorRegistrationSkillsModelPopulator assessorSkillsModelPopulator;

    private static final String FORM_ATTR_NAME = "form";

    @RequestMapping(method = RequestMethod.GET)
    public String getSkills(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationSkillsForm form) {
        model.addAttribute("model", assessorSkillsModelPopulator.populateModel());
        return "registration/innovation-areas";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitSkills(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorRegistrationSkillsForm form) {
        return "redirect:/registration/declaration";
    }

}