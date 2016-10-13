package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.assessment.form.profile.AssessorProfileTermsForm;
import com.worth.ifs.assessment.model.profile.AssessorProfileTermsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to manage the Assessor Profile Terms of Contract page
 */
@Controller
@RequestMapping("/profile/terms")
public class AssessorProfileTermsController {

    @Autowired
    private AssessorProfileTermsModelPopulator assessorProfileTermsModelPopulator;

    private static final String FORM_ATTR_NAME = "form";

    @RequestMapping(method = RequestMethod.GET)
    public String getTerms(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorProfileTermsForm form) {
        model.addAttribute("model", assessorProfileTermsModelPopulator.populateModel());
        return "profile/terms";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitTerms(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorProfileTermsForm form) {
        return "redirect:/assessor/dashboard";
    }

}