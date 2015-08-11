package com.worth.ifs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.domain.*;
import com.worth.ifs.repository.*;
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
@RequestMapping("/application")
public class ApplicationController {
    @Autowired
    ApplicationRepository repository;
    @Autowired
    UserApplicationRoleRepository userAppRoleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ResponseRepository responseRepository;

    @Autowired
    QuestionRepository questionRepository;

//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    @ResponseStatus(value = HttpStatus.NOT_FOUND)
//    public String handleException(Exception e) {
//        return e.getMessage();
//    }

    @RequestMapping("/id/{id}")
    public Application getApplicationById(@PathVariable("id") final Long id) {
        List<Application> apps = repository.findById(id);
        if(apps.size() == 0){
            return null;
        }else{
            return apps.get(0);
        }
    }

    @RequestMapping("/findResponsesByApplication/{applicationId}")
     public List<Response> findResponsesByApplication(@PathVariable("applicationId") final Long applicationId){
        Application app = repository.findOne(applicationId);
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

    @RequestMapping("/findAll")
     public List<Application> findAll() {
        List<Application> applications = repository.findAll();
        return applications;
    }

    @RequestMapping("/findByUser/{userId}")
    public List<Application> findByUserId(@PathVariable("userId") final Long userId) {
        User user = userRepository.findById(userId).get(0);
        List<UserApplicationRole> roles =  userAppRoleRepository.findByUser(user);
        List<Application> apps = new ArrayList<>();
        for (UserApplicationRole role : roles) {
            apps.add(role.getApplication());
        }
        return apps;
    }

    @RequestMapping(value = "/saveQuestionResponse", method = RequestMethod.POST)
    public ResponseEntity<String> saveQuestionResponse(@RequestBody JsonNode jsonObj) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");



        Long userId = jsonObj.get("userId").asLong();
        Long applicationId = jsonObj.get("applicationId").asLong();
        Long questionId = jsonObj.get("questionId").asLong();
        String value = jsonObj.get("value").asText("");

        System.out.println("Save response: "+applicationId+"/"+questionId+"/"+userId);

        User user = userRepository.findOne(userId);
        Application app = repository.findOne(applicationId);
        Question question = questionRepository.findOne(questionId);

        List<UserApplicationRole> userAppRoles = userAppRoleRepository.findByUserAndApplication(user, app);

        if(userAppRoles == null || userAppRoles.size()== 0){
            // user has no role on this application, so should not be able to write..
            return new ResponseEntity<String>(headers, HttpStatus.FORBIDDEN);
        }

        // get existing response to update.
        Response response = responseRepository.findByApplicationAndQuestion(app, question);
        if(response == null && userAppRoles != null && userAppRoles.size() > 0){
            response = new Response();
            response.setQuestion(question);
            response.setUserApplicationRole(userAppRoles.get(0));
            response.setApplication(app);
        }

        response.setUser(user);
        response.setValue(value);
        response.setDate(new Date());

        responseRepository.save(response);

        return new ResponseEntity<String>(headers, HttpStatus.ACCEPTED);
    }
}
