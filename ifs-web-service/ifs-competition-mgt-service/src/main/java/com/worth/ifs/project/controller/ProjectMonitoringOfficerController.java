package com.worth.ifs.project.controller;

import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.organisation.service.OrganisationAddressRestService;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.controller.form.ProjectMonitoringOfficerForm;
import com.worth.ifs.project.controller.viewmodel.ProjectMonitoringOfficerViewModel;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.OrganisationRestService;
import com.worth.ifs.user.service.ProcessRoleService;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

import static java.util.Arrays.asList;

/**
 * This controller will handle the management of the Monitoring Officer on projects
 */
@Controller
@RequestMapping("/project")
public class ProjectMonitoringOfficerController {

    static final String FORM_ATTR_NAME = "form";

	@Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private AddressRestService addressRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private OrganisationAddressRestService organisationAddressRestService;

    @RequestMapping(value = "/{projectId}/monitoring-officer", method = RequestMethod.GET)
    public String projectDetail(Model model, @PathVariable("projectId") final Long projectId,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(projectResource.getApplication());
        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(application.getCompetition());

        ProjectMonitoringOfficerViewModel viewModel = new ProjectMonitoringOfficerViewModel(projectResource.getName(), "TODO Area", projectResource.getAddress(),
                projectResource.getTargetStartDate(), "TODO Project Manager name", asList("TODO Org 1", "TODO Org 2"), competitionSummary);

        model.addAttribute("model", viewModel);
        return "project/monitoring-officer";
    }

    @RequestMapping(value = "/{projectId}/monitoring-officer", method = RequestMethod.POST)
    public String updateMonitoringOfficerDetails(Model model,
                                       @PathVariable("projectId") final Long projectId,
                                       @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectMonitoringOfficerForm form,
                                       BindingResult bindingResult,
                                       @ModelAttribute("loggedInUser") UserResource loggedInUser) {

//        Supplier<String> failureView = () -> doViewFinanceContact(model, projectId, form.getOrganisation(), loggedInUser, form, false);
//
//        if (bindingResult.hasErrors()) {
//            form.setBindingResult(bindingResult);
//            return failureView.get();
//        }
//
//        ServiceResult<Void> updateResult = projectService.updateFinanceContact(projectId, form.getOrganisation(), form.getFinanceContact());
//        return handleErrorsOrRedirectToProjectOverview("financeContact", projectId, model, form, bindingResult, updateResult, failureView);
        return "project/monitoring-officer";
    }

//	private String modelForFinanceContact(Model model, Long projectId, FinanceContactForm form, UserResource loggedInUser) {
//
//        ProjectResource projectResource = projectService.getById(projectId);
//        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
//		List<ProcessRoleResource> thisOrganisationUsers = userService.getOrganisationProcessRoles(applicationResource, form.getOrganisation());
//		CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());
//
//        model.addAttribute("organisationUsers", thisOrganisationUsers);
//        model.addAttribute(FORM_ATTR_NAME, form);
//        model.addAttribute("project", projectResource);
//        model.addAttribute("currentUser", loggedInUser);
//        model.addAttribute("app", applicationResource);
//        model.addAttribute("competition", competitionResource);
//        return "project/finance-contact";
//	}
}
