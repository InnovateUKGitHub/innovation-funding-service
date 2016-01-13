package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.ApplicationResourceHateoas;
import com.worth.ifs.application.resource.InviteCollaboratorResource;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.util.JsonStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.worth.ifs.util.JsonStatusResponse.accepted;
import static com.worth.ifs.util.JsonStatusResponse.internalServerError;

/**
 * ApplicationController exposes Application data and operations through a REST API.
 */
@RestController
@ExposesResourceFor(ApplicationResource.class)
@RequestMapping("/application")
public class ApplicationController {

    @Autowired
    ApplicationService applicationService;

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
        return applicationService.getApplicationById(id);
    }


    @RequestMapping("/")
    public List<ApplicationResource> findAll() {
        return applicationService.findAll();
    }

    @RequestMapping("/findByUser/{userId}")
    public List<ApplicationResource> findByUserId(@PathVariable("userId") final Long userId) {
        return applicationService.findByUserId(userId);
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
        return applicationService.getApplicationsByCompetitionIdAndUserId(competitionId, userId, role);
    }

    @RequestMapping(value = "/createApplicationByName/{competitionId}/{userId}", method = RequestMethod.POST)
    public ApplicationResource createApplicationByApplicationNameForUserIdAndCompetitionId(
            @PathVariable("competitionId") final Long competitionId,
            @PathVariable("userId") final Long userId,
            @RequestBody JsonNode jsonObj) {
        return applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(competitionId, userId, jsonObj);
    }

    @RequestMapping(value = "/{applicationId}/invitecollaborator", method = RequestMethod.POST)
    public JsonStatusResponse inviteCollaborator(
            @PathVariable("applicationId") final Long applicationId,
            @RequestBody InviteCollaboratorResource invite,
            HttpServletResponse response) {

        ServiceResult<Notification> notificationResult = applicationService.inviteCollaboratorToApplication(applicationId, invite);

        return notificationResult.mapLeftOrRight(
                failure -> internalServerError("Unable to send Notification to invitee", response),
                success -> accepted("Notification sent successfully", response)
        );
    }

}