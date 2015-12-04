package com.worth.ifs.application.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.controller.ApplicationController;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.hateoas.client.Hop.rel;

/**
 * ApplicationRestServiceImpl is a utility for CRUD operations on {@link Application}.
 * This class connects to the {@link ApplicationController}
 * through a REST call.
 */
@Service
public class ApplicationRestServiceImpl extends BaseRestService implements ApplicationRestService {
    @Value("${ifs.data.service.rest.application}")
    String applicationRestURL;

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public Application getApplicationById(Long applicationId) {
        return restGet(applicationRestURL + "/normal/" + applicationId, Application.class);
    }

    @Override
    public Application getApplicationByIdHateoas(Long applicationId) {
        ParameterizedTypeReference<Resources<ProcessRole>> typeReference =
            new ParameterizedTypeReference<Resources<ProcessRole>>(){};
        HttpHeaders headers = new HttpHeaders();
        headers.add("IFS_AUTH_TOKEN","123abc");
        Traverson traverson = new Traverson(URI.create(dataRestServiceURL + applicationRestURL + "/" + applicationId), MediaTypes.HAL_JSON);
        Application application = traverson.follow(rel("self")).withHeaders(headers)
                                            .toObject(ApplicationResource.class).toApplication();
        Resources<ProcessRole> roleResources = traverson.follow(rel("roles")).withHeaders(headers)
                                            .toObject(typeReference);
        List<ProcessRole> roles = new ArrayList<>(roleResources.getContent());
        application.setProcessRoles(roles);
        return application;

    }

    private ProcessRole getRole(String href){
        ParameterizedTypeReference<ProcessRoleResource> typeReference =
            new ParameterizedTypeReference<ProcessRoleResource>() {};
        ResponseEntity<ProcessRoleResource> resource = restGetParameterizedType(href, typeReference);
        return resource.getBody().toProcessRole();
    }

    @Override
    public List<Application> getApplicationsByUserId(Long userId) {
        return asList(restGet(applicationRestURL + "/findByUser/" + userId, Application[].class));
    }

    @Override
    public void saveApplication(Application application) {
        log.debug("ApplicationRestRestService.saveApplication " + application.getId());

        ResponseEntity<String> response =
                restPostWithEntity(applicationRestURL + "/saveApplicationDetails/" + application.getId(), application, String.class);

        logResponse(response);
    }

    @Override
    public void updateApplicationStatus(Long applicationId, Long statusId) {

        ResponseEntity<String> response =
                restGetEntity(applicationRestURL + "/updateApplicationStatus?applicationId=" + applicationId + "&statusId=" + statusId, String.class);

        log.debug("ApplicationRestRestService.updateApplicationStatus sending for applicationId " + applicationId);

        logResponse(response);
    }

    private void logResponse(ResponseEntity<String> response){
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("ApplicationRestRestService, save == ok : " + response.getBody());
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            //  bad credentials?
            log.info("Unauthorized request.");
        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.info("Status code not_found .....");
        }
    }

    @Override
    public Double getCompleteQuestionsPercentage(Long applicationId) {
        if (applicationId == null) {
            log.error("No application and/org organisation id!!");
        }

        ObjectNode jsonResponse = restGet(applicationRestURL + "/getProgressPercentageByApplicationId/" + applicationId, ObjectNode.class);
        return jsonResponse.get("completedPercentage").asDouble();
    }

    @Override
    public List<Application> getApplicationsByCompetitionIdAndUserId(Long competitionID, Long userID, UserRoleType role) {
        return asList(restGet(applicationRestURL + "/getApplicationsByCompetitionIdAndUserId/" + competitionID + "/" + userID + "/" + role, Application[].class));
    }

    @Override
    public Integer getAssignedQuestionsCount(Long applicationId, Long assigneeId) {
//        http://localhost:8090/questionStatuses/search/countByApplicationIdAndAssigneeId?applicationId=1&assigneeId=9

        String count = restGet("/questionStatuses/search/countByApplicationIdAndAssigneeId?applicationId=" + applicationId + "&assigneeId=" + assigneeId, String.class);
        return Integer.valueOf(count);
    }

    @Override
    public Application createApplication(Long competitionId, Long userId, String applicationName) {
        Application application = new Application();
        application.setName(applicationName);

        String url = applicationRestURL + "/createApplicationByName/" + competitionId + "/" + userId;

        return restPost(url, application, Application.class);
    }

}
