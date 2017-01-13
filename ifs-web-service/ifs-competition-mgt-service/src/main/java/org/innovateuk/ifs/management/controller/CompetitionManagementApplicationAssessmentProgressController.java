package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.form.AvailableAssessorsForm;
import org.innovateuk.ifs.management.model.ApplicationAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.model.ApplicationAvailableAssessorsModelPopulator;
import org.innovateuk.ifs.management.viewmodel.ApplicationAvailableAssessorsRowViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This controller will handle all Competition Management requests related to allocating assessors to an Application.
 */
@Controller
@RequestMapping("/competition/{competitionId}/application/{applicationId}/assessors")
public class CompetitionManagementApplicationAssessmentProgressController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private ApplicationAssessmentProgressModelPopulator applicationAssessmentProgressModelPopulator;

    @Autowired
    private ApplicationAvailableAssessorsModelPopulator applicationAvailableAssessorsModelPopulator;

    @RequestMapping(method = RequestMethod.GET)
    public String applicationProgress(Model model,
                                      @Valid @ModelAttribute(FORM_ATTR_NAME) AvailableAssessorsForm form,
                                      @SuppressWarnings("unused") BindingResult bindingResult,
                                      ValidationHandler validationHandler,
                                      @PathVariable("applicationId") Long applicationId) {
        model.addAttribute("model", applicationAssessmentProgressModelPopulator.populateModel(applicationId));
        model.addAttribute("available", applicationAvailableAssessorsModelPopulator.populateModel(new CompetitionResource(), sortFieldForAvailableAssessors(form.getSort())));
        model.addAttribute("activeSortField", sortFieldForAvailableAssessors(form.getSort()));
        return "competition/application-progress";
    }

    private String sortFieldForAvailableAssessors(String sort) {
        return activeSortField(sort,  "title", "skills", "totalApplications", "assignedApplications", "acceptedApplications");
    }

    private String activeSortField(String givenField, String defaultField, String... allowedFields) {
        return Arrays.stream(allowedFields)
                .filter(x -> givenField != null && givenField.equals(x))
                .findAny()
                .orElse(defaultField);
    }
}