package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.form.AvailableAssessorsForm;
import org.innovateuk.ifs.management.model.ApplicationAssessmentProgressModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Arrays;

/**
 * This controller will handle all Competition Management requests related to allocating assessors to an Application.
 */
@Controller
@RequestMapping("/competition/{competitionId}/application/{applicationId}/assessors")
public class CompetitionManagementApplicationAssessmentProgressController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private ApplicationAssessmentProgressModelPopulator applicationAssessmentProgressModelPopulator;

    @RequestMapping(method = RequestMethod.GET)
    public String applicationProgress(Model model,
                                      @Valid @ModelAttribute(FORM_ATTR_NAME) AvailableAssessorsForm form,
                                      @SuppressWarnings("unused") BindingResult bindingResult,
                                      @PathVariable("applicationId") Long applicationId) {
        model.addAttribute("model", applicationAssessmentProgressModelPopulator.populateModel(applicationId, sortFieldForAvailableAssessors(form.getSort())));
        model.addAttribute("activeSortField", sortFieldForAvailableAssessors(form.getSort()));
        return "competition/application-progress";
    }

    private String sortFieldForAvailableAssessors(String sort) {
        return activeSortField(sort,  "title", "skills", "totalApplications", "assignedApplications", "acceptedApplications");
    }

    private String activeSortField(String givenField, String defaultField, String... allowedFields) {
        return Arrays.stream(allowedFields)
                .filter(field -> givenField != null && givenField.equals(field))
                .findAny()
                .orElse(defaultField);
    }
}