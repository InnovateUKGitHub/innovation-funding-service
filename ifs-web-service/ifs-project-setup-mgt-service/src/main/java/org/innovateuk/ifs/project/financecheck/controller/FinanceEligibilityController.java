package org.innovateuk.ifs.project.financecheck.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.OpenFinanceSectionModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.innovateuk.ifs.application.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller is for allowing internal users to view and update application finances entered by applicants
 */
@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}")
public class FinanceEligibilityController {
    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private OpenFinanceSectionModelPopulator openFinanceSectionModel;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProjectService projectService;

    @RequestMapping(value = "/finance-eligibility", method = GET)
    public String view(@PathVariable("projectId") final Long projectId, @PathVariable("organisationId") Long organisationId,
                       @ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
                       BindingResult bindingResult,
                       @ModelAttribute("loggedInUser") UserResource loggedInUser,
                       Model model,
                       HttpServletRequest request){
        ProjectResource project = projectService.getById(projectId);
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(project.getApplication());
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(application.getCompetition());

        SectionResource section = simpleFilter(allSections, s -> s.getType().equals(FINANCE)).get(0);

        openFinanceSectionModel.populateModel(form, model, application, section, user, bindingResult, allSections, organisationId, true);

        return "project/financecheck/eligibility";
    }
}