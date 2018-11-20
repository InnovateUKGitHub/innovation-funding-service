package org.innovateuk.ifs.application.forms.academiccosts.controller;

import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.application.forms.academiccosts.populator.AcademicCostFormPopulator;
import org.innovateuk.ifs.application.forms.academiccosts.populator.AcademicCostViewModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/academic-costs/organisation/{organisationId}/section/{sectionId}")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "ACADEMIC_COSTS_APPLICANT", description = "Applicants can all fill out the Your project costs section of the application.")
public class AcademicCostsController {

    private static final String VIEW = "application/academic-costs";

    @Autowired
    private AcademicCostFormPopulator formPopulator;

    @Autowired
    private AcademicCostViewModelPopulator viewModelPopulator;

    @GetMapping
    public String viewAcademicCosts(Model model,
                                    UserResource user,
                                    @PathVariable long applicationId,
                                    @PathVariable long organisationId,
                                    @PathVariable long sectionId,
                                    @ModelAttribute("form") AcademicCostForm form) {
        formPopulator.populate(form, applicationId, organisationId);
        model.addAttribute("model", viewModelPopulator.populate(organisationId, applicationId, sectionId, user.isInternalUser()));
        return VIEW;
    }
}
