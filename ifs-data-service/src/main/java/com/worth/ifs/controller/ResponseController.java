package com.worth.ifs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.domain.*;
import com.worth.ifs.repository.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    @RequestMapping(value = "/saveQuestionResponse", method = RequestMethod.POST)
    public ResponseEntity<String> saveQuestionResponse(@RequestBody JsonNode jsonObj) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        Long userId = jsonObj.get("userId").asLong();
        Long applicationId = jsonObj.get("applicationId").asLong();
        Long questionId = jsonObj.get("questionId").asLong();
        String value = jsonObj.get("value").asText("");

        log.info("Save response: "+applicationId+"/"+questionId+"/"+userId);

        User user = userRepository.findOne(userId);
        Application application = applicationRepository.findOne(applicationId);
        Question question = questionRepository.findOne(questionId);

        List<UserApplicationRole> userAppRoles = userAppRoleRepository.findByUserAndApplication(user, application);

        if(userAppRoles == null || userAppRoles.size()== 0){
            // user has no role on this application, so should not be able to write..
            return new ResponseEntity<String>(headers, HttpStatus.FORBIDDEN);
        }

        // get existing response to update.
        Response response = responseRepository.findByApplicationAndQuestion(application, question);
        if(response == null){
            response = new Response(null, new Date(), value, false, userAppRoles.get(0), question, application);
        }else{
            response.setValue(value);
            response.setDate(new Date());
            response.setUserApplicationRole(userAppRoles.get(0));
        }
        
        responseRepository.save(response);

        return new ResponseEntity<String>(headers, HttpStatus.ACCEPTED);
    }
}
