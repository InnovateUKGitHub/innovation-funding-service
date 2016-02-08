package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.RestResultBuilder;
import com.worth.ifs.user.domain.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;

/**
 * ApplicationController exposes Application data and operations through a REST API.
 */
@RestController
@ExposesResourceFor(ApplicationResource.class)
@RequestMapping("/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping("/normal/{id}")
    public RestResult<ApplicationResource> getApplicationById(@PathVariable("id") final Long id) {
        return newRestHandler(ApplicationResource.class).perform(() -> applicationService.getApplicationById(id));
    }


    @RequestMapping("/")
    public RestResult<List<ApplicationResource>> findAll() {
        return applicationResourceHandler().perform(() -> applicationService.findAll());
    }

    @RequestMapping("/findByUser/{userId}")
    public RestResult<List<ApplicationResource>> findByUserId(@PathVariable("userId") final Long userId) {
        return applicationResourceHandler().perform(() -> applicationService.findByUserId(userId));
    }

    @RequestMapping("/saveApplicationDetails/{id}")
    public RestResult<Void> saveApplicationDetails(@PathVariable("id") final Long id,
                                                   @RequestBody ApplicationResource application) {

        RestResultBuilder<ApplicationResource, Void> handler = newRestHandler();
        return handler.perform(() -> applicationService.saveApplicationDetails(id, application));
    }

    @RequestMapping("/getProgressPercentageByApplicationId/{applicationId}")
    public RestResult<ObjectNode> getProgressPercentageByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        return newRestHandler(ObjectNode.class).perform(() -> applicationService.getProgressPercentageNodeByApplicationId(applicationId));
    }

    @RequestMapping(value = "/updateApplicationStatus", method = RequestMethod.GET)
    public RestResult<Void> updateApplicationStatus(@RequestParam("applicationId") final Long id,
                                                          @RequestParam("statusId") final Long statusId) {

        RestResultBuilder<ApplicationResource, Void> handler = newRestHandler();
        return handler.perform(() ->applicationService.updateApplicationStatus(id, statusId));
    }


    @RequestMapping("/applicationReadyForSubmit/{applicationId}")
    public RestResult<ObjectNode> applicationReadyForSubmit(@PathVariable("applicationId") final Long id){
        return newRestHandler(ObjectNode.class).perform(() -> applicationService.applicationReadyForSubmit(id));
    }


    @RequestMapping("/getApplicationsByCompetitionIdAndUserId/{competitionId}/{userId}/{role}")
    public RestResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserId(@PathVariable("competitionId") final Long competitionId,
                                                                     @PathVariable("userId") final Long userId,
                                                                     @PathVariable("role") final UserRoleType role) {

        return applicationResourceHandler().perform(() -> applicationService.getApplicationsByCompetitionIdAndUserId(competitionId, userId, role));
    }

    @RequestMapping(value = "/createApplicationByName/{competitionId}/{userId}", method = RequestMethod.POST)
    public RestResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionId(
            @PathVariable("competitionId") final Long competitionId,
            @PathVariable("userId") final Long userId,
            @RequestBody JsonNode jsonObj) {

        return newRestHandler(ApplicationResource.class).perform(() -> applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(competitionId, userId, jsonObj.get("name").textValue()));
    }

    private RestResultBuilder<List<ApplicationResource>, List<ApplicationResource>> applicationResourceHandler() {
        return newRestHandler();
    }
}