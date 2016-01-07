package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.ApplicationResourceHateoas;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.user.domain.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * ApplicationController exposes Application data and operations through a REST API.
 */
@RestController
@ExposesResourceFor(ApplicationResource.class)
@RequestMapping("/application")
public class ApplicationController {

    @Autowired
    ApplicationService applicationService;

    @Autowired
    ApplicationMapper applicationMapper;

    @RequestMapping("/{id}")
    public ApplicationResourceHateoas getApplicationByIdHateoas(@PathVariable("id") final Long id) {
        return applicationService.getApplicationByIdHateoas(id);

    }

    @RequestMapping("/hateoas/")
    public Resources<ApplicationResourceHateoas> findAllHateoas() {
        return applicationService.findAllHateoas();
    }

    @RequestMapping("/normal/{id}")
    public ApplicationResource getApplicationById(@PathVariable("id") final Long id) {
        Application application = applicationService.getApplicationById(id);
        ApplicationResource applicationResource = applicationMapper.mapApplicationToResource(application);
        return applicationResource;
    }


    @RequestMapping("/")
    public List<ApplicationResource> findAll() {
        return simpleMap(applicationService.findAll(), applicationMapper::mapApplicationToResource);
    }

    @RequestMapping("/findByUser/{userId}")
    public List<ApplicationResource> findByUserId(@PathVariable("userId") final Long userId) {
        return simpleMap(applicationService.findByUserId(userId), applicationMapper::mapApplicationToResource);
    }

    @RequestMapping("/saveApplicationDetails/{id}")
    public ResponseEntity<String> saveApplicationDetails(@PathVariable("id") final Long id,
                                                         @RequestBody ApplicationResource application) {
        return applicationService.saveApplicationDetails(id, application);
    }


    @RequestMapping("/getProgressPercentageByApplicationId/{applicationId}")
    public ObjectNode getProgressPercentageByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        return applicationService.getProgressPercentageByApplicationId(applicationId);
    }

    @RequestMapping(value = "/updateApplicationStatus", method = RequestMethod.GET)
    public ResponseEntity<String> updateApplicationStatus(@RequestParam("applicationId") final Long id,
                                                          @RequestParam("statusId") final Long statusId) {

        return applicationService.updateApplicationStatus(id, statusId);
    }


    @RequestMapping("/getApplicationsByCompetitionIdAndUserId/{competitionId}/{userId}/{role}")
    public List<ApplicationResource> getApplicationsByCompetitionIdAndUserId(@PathVariable("competitionId") final Long competitionId,
                                                                     @PathVariable("userId") final Long userId,
                                                                     @PathVariable("role") final UserRoleType role) {
        return simpleMap(applicationService.getApplicationsByCompetitionIdAndUserId(competitionId, userId, role), applicationMapper::mapApplicationToResource);
    }

    @RequestMapping(value = "/createApplicationByName/{competitionId}/{userId}", method = RequestMethod.POST)
    public ApplicationResource createApplicationByApplicationNameForUserIdAndCompetitionId(
            @PathVariable("competitionId") final Long competitionId,
            @PathVariable("userId") final Long userId,
            @RequestBody JsonNode jsonObj) {
        return applicationMapper.mapApplicationToResource(applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(competitionId, userId, jsonObj));
    }

    private class Bluh {
        private String name = "hello";

        protected String getName(){return name;}
    }
}