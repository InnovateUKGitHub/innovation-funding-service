package org.innovateuk.ifs.project.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProjectController exposes Project data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/{id}")
    public RestResult<ProjectResource> getProjectById(@PathVariable("id") final Long id) {
        return projectService.getProjectById(id).toGetResponse();
    }

    @GetMapping("/application/{application}")
    public RestResult<ProjectResource> getByApplicationId(@PathVariable("application") final Long application) {
        return projectService.getByApplicationId(application).toGetResponse();
    }

    @GetMapping("/")
    public RestResult<List<ProjectResource>> findAll() {
        return projectService.findAll().toGetResponse();
    }

    @GetMapping(value = "/user/{userId}")
    public RestResult<List<ProjectResource>> findByUserId(@PathVariable("userId") final Long userId) {
        return projectService.findByUserId(userId).toGetResponse();
    }

    @GetMapping("/{projectId}/project-users")
    public RestResult<List<ProjectUserResource>> getProjectUsers(@PathVariable("projectId") final Long projectId) {
        return projectService.getProjectUsers(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}/getOrganisationByUser/{userId}")
    public RestResult<OrganisationResource> getOrganisationByProjectAndUser(@PathVariable("projectId") final Long projectId,
                                                                            @PathVariable("userId") final Long userId){
        return projectService.getOrganisationByProjectAndUser(projectId, userId).toGetResponse();
    }

    @GetMapping("/{projectId}/project-manager")
    public RestResult<ProjectUserResource> getProjectManager(@PathVariable(value = "projectId") Long projectId) {
        return projectService.getProjectManager(projectId).toGetResponse();
    }

/*    @GetMapping("/competition/{competitionId}/pending-spend-profile-generation")
    public RestResult<ProjectUserResource> getPendingSpendProfileGenerationByCompetitionId(@PathVariable(value = "competitionId") Long competitionId) {
        return projectService.getProjectManager(projectId).toGetResponse();
    }*/
}
