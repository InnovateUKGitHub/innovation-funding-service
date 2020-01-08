package org.innovateuk.ifs.project.core.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
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
    public RestResult<ProjectResource> getProjectById(@PathVariable long id) {
        return projectService.getProjectById(id).toGetResponse();
    }

    @GetMapping("/application/{applicationId}")
    public RestResult<ProjectResource> getByApplicationId(@PathVariable long applicationId) {
        return projectService.getByApplicationId(applicationId).toGetResponse();
    }

    @GetMapping
    public RestResult<List<ProjectResource>> findAll() {
        return projectService.findAll().toGetResponse();
    }

    @GetMapping(value = "/user/{userId}")
    public RestResult<List<ProjectResource>> findByUserId(@PathVariable long userId) {
        return projectService.findByUserId(userId).toGetResponse();
    }

    @GetMapping("/{projectId}/project-users")
    public RestResult<List<ProjectUserResource>> getProjectUsers(@PathVariable long projectId) {
        return projectService.getProjectUsers(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}/get-organisation-by-user/{userId}")
    public RestResult<OrganisationResource> getOrganisationByProjectAndUser(@PathVariable long projectId,
                                                                            @PathVariable long userId){
        return projectService.getOrganisationByProjectAndUser(projectId, userId).toGetResponse();
    }

    @PostMapping("/create-project/application/{applicationId}")
    public RestResult<ProjectResource> createProjectFromApplication(@PathVariable long applicationId) {
        return projectService.createProjectFromApplication(applicationId).toPostWithBodyResponse();
    }

    @GetMapping("/{projectId}/lead-organisation")
    public RestResult<OrganisationResource> getLeadOrganisation(@PathVariable long projectId){
        return projectService.getLeadOrganisation(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}/user/{organisationId}/application-exists")
    public RestResult<Boolean> existsOnApplication(@PathVariable long projectId, @PathVariable long organisationId){
        return projectService.existsOnApplication(projectId, organisationId).toGetResponse();
    }
}