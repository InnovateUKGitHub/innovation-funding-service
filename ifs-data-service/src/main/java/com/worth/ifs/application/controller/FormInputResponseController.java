package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.transactional.AssessorService;
import com.worth.ifs.transactional.ServiceLocator;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ApplicationController exposes Application data and operations through a REST API.
 */
@RestController
@RequestMapping("/forminputresponse")
public class FormInputResponseController {
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    ProcessRoleRepository processRoleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FormInputResponseRepository responseRepository;
    @Autowired
    FormInputRepository questionRepository;
    @Autowired
    AssessorService assessorService;

    @Autowired
    ServiceLocator serviceLocator;

    QuestionController questionController = new QuestionController();

    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping("/findResponsesByApplication/{applicationId}")
    public List<FormInputResponse> findResponsesByApplication(@PathVariable("applicationId") final Long applicationId) {
        Application app = applicationRepository.findOne(applicationId);
        List<ProcessRole> userAppRoles = app.getProcessRoles();

        List<FormInputResponse> responses = new ArrayList<>();
        for (ProcessRole userAppRole : userAppRoles) {
            responses.addAll(responseRepository.findByUpdatedBy(userAppRole));
        }
        return responses;
    }

    private FormInputResponse getOrCreateResponse(Long applicationId, Long userId, Long formInputId) {
        Application application = applicationRepository.findOne(applicationId);
        FormInput formInput = questionRepository.findOne(formInputId);
        User user = userRepository.findOne(userId);

        List<ProcessRole> userAppRoles = processRoleRepository.findByUserAndApplication(user, application);
        if (userAppRoles == null || userAppRoles.size() == 0) {
            // user has no role on this application, so should not be able to write..
            log.error("FORBIDDEN TO SAVE");
            return null;
        }

        FormInputResponse response = responseRepository.findByApplicationAndFormInput(application, formInput);
        if (response == null) {
            response = new FormInputResponse(LocalDateTime.now(), "", userAppRoles.get(0), formInput, application);
        }

        return response;
    }

    @RequestMapping(value = "/saveQuestionResponse", method = RequestMethod.POST)
    public ResponseEntity<String> saveQuestionResponse(@RequestBody JsonNode jsonObj) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Long userId = jsonObj.get("userId").asLong();
        Long applicationId = jsonObj.get("applicationId").asLong();
        Long formInputId = jsonObj.get("formInputId").asLong();
        String value = jsonObj.get("value").asText("");
        value = HtmlUtils.htmlUnescape(value);

        log.info("Save response: " + applicationId + "/" + formInputId + "/" + userId);

        User user = userRepository.findOne(userId);
        Application application = applicationRepository.findOne(applicationId);
        List<ProcessRole> userAppRoles = processRoleRepository.findByUserAndApplication(user, application);

        FormInputResponse response = this.getOrCreateResponse(applicationId, userId, formInputId);
        if (response == null) {
            log.error("FORBIDDEN TO SAVE");
            return new ResponseEntity<String>(headers, HttpStatus.FORBIDDEN);
        }

        if (!response.getValue().equals(value)) {
            response.setUpdateDate(LocalDateTime.now());
            response.setUpdatedBy(userAppRoles.get(0));
        }
        response.setValue(value);

        responseRepository.save(response);

        log.warn("Single question saved!");

        return new ResponseEntity<String>(headers, HttpStatus.ACCEPTED);
    }

}