package com.worth.ifs.project.controller;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.transactional.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * ProjectController exposes Project data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @RequestMapping("/{id}")
    public RestResult<ProjectResource> getProjectById(@PathVariable("id") final Long id) {
        return projectService.getProjectById(id).toGetResponse();
    }

    @RequestMapping("/")
    public RestResult<List<ProjectResource>> findAll() {
        return projectService.findAll().toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/startdate", method = POST)
    public RestResult<Void> updateProjectStartDate(@PathVariable("projectId") final Long projectId,
                                                   @RequestParam("projectStartDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate projectStartDate) {
        return projectService.updateProjectStartDate(projectId, projectStartDate).toPostResponse();
    }

    @RequestMapping(value = "/{projectId}/address", method = POST)
    public RestResult<Void> updateProjectAddress(@PathVariable("projectId") final Long projectId,
                                                 @RequestParam("addressType") final String addressType,
                                                 @RequestBody AddressResource addressResource) {
        return projectService.updateProjectAddress(projectId, AddressType.valueOf(addressType), addressResource).toPostResponse();
    }
}
