package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.assessment.form.profile.AssessorProfileDeclarationForm;
import com.worth.ifs.assessment.model.profile.AssessorProfileDeclarationModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to manage the Assessor Profile Declaration of Interest page
 */
@Controller
@RequestMapping("/profile/declaration")
public class AssessorProfileDeclarationController {

    @Autowired
    private AssessorProfileDeclarationModelPopulator assessorProfileDeclarationModelPopulator;

    private static final String FORM_ATTR_NAME = "form";

    @RequestMapping(method = RequestMethod.GET)
    public String getDeclaration(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form) {
        model.addAttribute("model", assessorProfileDeclarationModelPopulator.populateModel());
        return "profile/declaration-of-interest";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitDeclaration(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form) {
        return "redirect:/registration/terms";
    }

}