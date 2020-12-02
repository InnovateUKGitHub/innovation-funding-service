package org.innovateuk.ifs.project.projectteam.controller;


import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.projectdetails.form.ProjectManagerForm;
import org.innovateuk.ifs.project.projectteam.viewmodel.ProjectManagerViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.toField;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleAnyMatch;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * This controller will handle all requests that are related to the project manager.
 */
@Controller
@RequestMapping("/project")
public class ProjectManagerController {

    private ProjectService projectService;
    private ProjectDetailsService projectDetailsService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public ProjectManagerController(
            ProjectService projectService,
            ProjectDetailsService projectDetailsService
    ) {
        this.projectService = projectService;
        this.projectDetailsService = projectDetailsService;
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_MANAGER_PAGE')")
    @GetMapping("/{projectId}/team/project-manager")
    public String viewProjectTeam(@PathVariable("projectId") final long projectId,
                                  Model model,
                                  @ModelAttribute(name = "form", binding = false) ProjectManagerForm projectManagerForm,
                                  UserResource loggedInUser) {
        populateOriginalProjectManagerForm(projectId, projectManagerForm);
        return doViewProjectManager(model, projectId, loggedInUser);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_MANAGER_PAGE')")
    @PostMapping(value = "/{projectId}/team/project-manager")
    public String updateProjectManager(@PathVariable("projectId") final long projectId, Model model,
                                       @Valid @ModelAttribute("form") ProjectManagerForm projectManagerForm,
                                       @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                       UserResource loggedInUser) {
        Supplier<String> failureView = () -> doViewProjectManager(model, projectId, loggedInUser);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> updateResult = projectDetailsService.updateProjectManager(projectId, projectManagerForm.getProjectManager());

            return validationHandler.addAnyErrors(updateResult, toField("projectManager")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectTeamPage(projectId));
        });
    }

    private void populateOriginalProjectManagerForm(final long projectId, ProjectManagerForm projectManagerForm) {
        Optional<ProjectUserResource> existingProjectManager = getProjectManager(projectId);
        projectManagerForm.setProjectManager(existingProjectManager.map(ProjectUserResource::getUser).orElse(null));
    }

    private Optional<ProjectUserResource> getProjectManager(long projectId) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getId() == pu.getRole());
    }

    private String doViewProjectManager(Model model, long projectId, UserResource loggedInUser) {

        List<ProjectUserResource> leadOrgPartners = projectService.getLeadPartners(projectId);
        if(!userIsLeadPartner(leadOrgPartners, loggedInUser)) {
            return redirectToProjectTeamPage(projectId);
        }

        populateProjectManagerModel(model, projectId, leadOrgPartners);
        return "project/team/project-manager";
    }

    private boolean userIsLeadPartner(List<ProjectUserResource> leadOrgPartners, UserResource user) {
        return simpleAnyMatch(leadOrgPartners,
                              projectUser -> projectUser.getUser().equals(user.getId()));
    }

    private String redirectToProjectTeamPage(long projectId) {
        return "redirect:/project/" + projectId + "/team";
    }

    private void populateProjectManagerModel(Model model, final long projectId, final List<ProjectUserResource> leadOrgPartners) {

        ProjectResource projectResource = projectService.getById(projectId);
        CompetitionResource competition = competitionRestService.getCompetitionById(projectResource.getCompetition()).getSuccess();
        ProjectManagerViewModel viewModel = new ProjectManagerViewModel(leadOrgPartners, projectResource.getId(), projectResource.getName(), competition.isLoan(), competition.isKtp());
        model.addAttribute("model", viewModel);
    }

}
