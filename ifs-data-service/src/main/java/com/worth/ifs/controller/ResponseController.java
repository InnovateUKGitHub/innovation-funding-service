package com.worth.ifs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.domain.*;
import com.worth.ifs.repository.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ApplicationController exposes Application data through a REST API.
 */
@RestController
@RequestMapping("/response")
public class ResponseController {
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    UserApplicationRoleRepository userAppRoleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ResponseRepository responseRepository;
    @Autowired
    QuestionRepository questionRepository;

    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping("/findResponsesByApplication/{applicationId}")
     public List<Response> findResponsesByApplication(@PathVariable("applicationId") final Long applicationId){
        Application app = applicationRepository.findOne(applicationId);
        List<UserApplicationRole> userAppRoles = app.getUserApplicationRoles();

        List<Response> responses = new ArrayList<Response>();
        for (UserApplicationRole userAppRole : userAppRoles) {
            responses.addAll(responseRepository.findByUserApplicationRole(userAppRole));
        }
        return responses;
    }

    @RequestMapping("/findResponsesByApplication/{applicationId}/section/{sectionId}")
    public List<Response> findResponsesByApplication(@PathVariable("applicationId") final Long applicationId, @PathVariable("sectionId") final Long sectionId){
        throw new NotImplementedException();
    }

    @RequestMapping(value="/assignQuestion")
    public ResponseEntity<String> assignQuestion(@RequestParam("applicationId") final Long applicationId,
                                                         @RequestParam("questionId") final Long questionId,
                                                         @RequestParam("userId") final Long userId,
                                                         @RequestParam("assigneeId") final Long assigneeId){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        User assignee = userRepository.findOne(assigneeId);

        Response response = this.getOrCreateResponse(applicationId, userId, questionId);
        if(response == null){
            log.error("FORBIDDEN TO SAVE");
            return new ResponseEntity<String>(headers, HttpStatus.FORBIDDEN);
        }

        response.setDate(LocalDateTime.now());
        response.setAssignee(assignee);
        response.setAssignedDate(LocalDateTime.now());

        responseRepository.save(response);

        return new ResponseEntity<String>(headers, HttpStatus.OK);

    }
    private Response getOrCreateResponse(Long applicationId, Long userId,  Long questionId){
        Application application = applicationRepository.findOne(applicationId);
        Question question = questionRepository.findOne(questionId);
        User user = userRepository.findOne(userId);


        List<UserApplicationRole> userAppRoles = userAppRoleRepository.findByUserAndApplication(user, application);
        if(userAppRoles == null || userAppRoles.size()== 0){
            // user has no role on this application, so should not be able to write..
            log.error("FORBIDDEN TO SAVE");
            return null;
        }

        Response response = responseRepository.findByApplicationAndQuestion(application, question);
        if(response == null){
            response = new Response(null, LocalDateTime.now(), "", false, userAppRoles.get(0), question, application);
        }

        return response;
    }

    @RequestMapping(value="/markResponseAsComplete")
    public ResponseEntity<String> markResponseAsComplete(@RequestParam("applicationId") final Long applicationId,
                                                         @RequestParam("questionId") final Long questionId,
                                                         @RequestParam("userId") final Long userId,
                                                         @RequestParam(value = "isComplete", required = false) Boolean markedAsComplete){
        if(markedAsComplete == null){
            markedAsComplete = true;
        }
        log.info("Mark:::" + markedAsComplete);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        User user = userRepository.findOne(userId);

        Application application = applicationRepository.findOne(applicationId);
        List<UserApplicationRole> userAppRoles = userAppRoleRepository.findByUserAndApplication(user, application);

        Response response = this.getOrCreateResponse(applicationId, userId, questionId);
        if(response == null){
            log.error("FORBIDDEN TO SAVE");
            return new ResponseEntity<String>(headers, HttpStatus.FORBIDDEN);
        }


        response.setMarkedAsComplete(markedAsComplete);
        response.setDate(LocalDateTime.now());
        response.setUserApplicationRole(userAppRoles.get(0));
        responseRepository.save(response);

        return new ResponseEntity<String>(headers, HttpStatus.OK);

    }

    @RequestMapping(value = "/saveQuestionResponse", method = RequestMethod.POST)
    public ResponseEntity<String> saveQuestionResponse(@RequestBody JsonNode jsonObj) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Long userId = jsonObj.get("userId").asLong();
        Long applicationId = jsonObj.get("applicationId").asLong();
        Long questionId = jsonObj.get("questionId").asLong();
        String value = jsonObj.get("value").asText("");
        value = HtmlUtils.htmlUnescape(value);

        log.info("Save response: "+applicationId+"/"+questionId+"/"+userId);

        User user = userRepository.findOne(userId);
        Application application = applicationRepository.findOne(applicationId);
        Question question = questionRepository.findOne(questionId);

        List<UserApplicationRole> userAppRoles = userAppRoleRepository.findByUserAndApplication(user, application);

        Response response = this.getOrCreateResponse(applicationId, userId, questionId);
        if(response == null){
            log.error("FORBIDDEN TO SAVE");
            return new ResponseEntity<String>(headers, HttpStatus.FORBIDDEN);
        }


        response.setValue(value);
        response.setDate(LocalDateTime.now());
        response.setUserApplicationRole(userAppRoles.get(0));

        
        responseRepository.save(response);

        log.warn("Single question saved!");

        return new ResponseEntity<String>(headers, HttpStatus.ACCEPTED);
    }
}
