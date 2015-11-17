package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.transactional.AssessorService;
import com.worth.ifs.transactional.ServiceLocator;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.validator.ResponseValidator;
import com.worth.ifs.validator.ValidatedResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.user.domain.UserRoleType.APPLICANT;

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

        Optional<ProcessRole> applicantProcessRole = userAppRoles.stream().filter(processRole -> processRole.getRole().getName().equals(APPLICANT.getName())).findFirst();

        Optional<FormInputResponse> response = applicantProcessRole.map(role -> {
            FormInputResponse existingResponse = responseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(application.getId(), userAppRoles.get(0).getId(), formInput.getId());
            return existingResponse != null ? existingResponse : new FormInputResponse(LocalDateTime.now(), "", role, formInput, application);
        });

        return response.orElseGet(() -> {
            log.error("No Applicant Process Role on user when trying to create a FormInputResponse");
            return null;
        });
    }

    @RequestMapping(value = "/saveQuestionResponse", method = RequestMethod.POST)
    public List<String> saveQuestionResponse(@RequestBody JsonNode jsonObj, BindingResult bindingResult, HttpServletResponse servletResponse) {
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
            servletResponse.setStatus( HttpServletResponse.SC_FORBIDDEN  );
            return null;
        }

        if (!response.getValue().equals(value)) {
            response.setUpdateDate(LocalDateTime.now());
            response.setUpdatedBy(userAppRoles.get(0));
        }

        response.setValue(value);

        ResponseValidator responseValidator = new ResponseValidator();
        responseValidator.validate(response, bindingResult);

        if(bindingResult.hasErrors()){
            log.warn("Got validation errors: ");
            bindingResult.getAllErrors().stream().forEach(e -> log.warn("Validation: "+ e.getDefaultMessage()));
        }else{
            responseRepository.save(response);
            log.info("Single question saved!");
        }
        ValidatedResponse validatedResponse = new ValidatedResponse(bindingResult, response);
        servletResponse.setStatus(HttpServletResponse.SC_ACCEPTED);
        return validatedResponse.getAllErrors();
    }

}