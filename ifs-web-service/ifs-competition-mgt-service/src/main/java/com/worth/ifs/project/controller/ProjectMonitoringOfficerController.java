package com.worth.ifs.project.controller;

import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.organisation.service.OrganisationAddressRestService;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.controller.form.ProjectMonitoringOfficerForm;
import com.worth.ifs.project.controller.viewmodel.ProjectMonitoringOfficerViewModel;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.OrganisationResource;
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
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

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
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(application.getCompetition());
        String projectManagerName = getProjectManagerName(projectId, projectResource);
        List<String> partnerOrganisationNames = getPartnerOrganisationNames(projectId);
        String innovationArea = competition.getInnovationAreaName();

        ProjectMonitoringOfficerViewModel viewModel = new ProjectMonitoringOfficerViewModel(projectResource.getName(),
                innovationArea, projectResource.getAddress(), projectResource.getTargetStartDate(), projectManagerName,
                partnerOrganisationNames, competitionSummary);

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

    private String getProjectManagerName(@PathVariable("projectId") Long projectId, ProjectResource projectResource) {
        String projectManagerName;
        Long projectManagerId = projectResource.getProjectManager();
        if (projectManagerId != null) {
            List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
            Optional<ProjectUserResource> projectManager = simpleFindFirst(projectUsers, pu -> projectManagerId.equals(pu.getUser()));
            projectManagerName = projectManager.map(ProjectUserResource::getRoleName).orElse("");
        } else {
            projectManagerName = "";
        }
        return projectManagerName;
    }

    private List<String> getPartnerOrganisationNames(@PathVariable("projectId") Long projectId) {
        List<OrganisationResource> partnerOrganisations = projectService.getPartnerOrganisationsForProject(projectId);
        return simpleMap(partnerOrganisations, OrganisationResource::getName);
    }
}
