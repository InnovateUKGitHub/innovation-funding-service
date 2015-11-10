package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.transactional.AssessorService;
import com.worth.ifs.transactional.ServiceLocator;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.util.Either;
import com.worth.ifs.util.JsonStatusResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.util.Either.getLeftOrRight;
import static com.worth.ifs.util.Either.right;
import static com.worth.ifs.util.EntityLookupCallbackFunctions.withProcessRoleReturnJsonResponse;

/**
 * ApplicationController exposes Application data and operations through a REST API.
 */
@RestController
@RequestMapping("/response")
public class ResponseController {
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    ProcessRoleRepository processRoleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ResponseRepository responseRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    AssessorService assessorService;

    @Autowired
    ServiceLocator serviceLocator;

    QuestionController questionController = new QuestionController();

    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping("/findResponsesByApplication/{applicationId}")
    public List<Response> findResponsesByApplication(@PathVariable("applicationId") final Long applicationId){
        Application app = applicationRepository.findOne(applicationId);
        List<ProcessRole> userAppRoles = app.getProcessRoles();

        List<Response> responses = new ArrayList<Response>();
        for (ProcessRole userAppRole : userAppRoles) {
            responses.addAll(responseRepository.findByUpdatedBy(userAppRole));
        }
        return responses;
    }

    private Response getOrCreateResponse(Long applicationId, Long userId,  Long questionId){
        Application application = applicationRepository.findOne(applicationId);
        Question question = questionRepository.findOne(questionId);
        User user = userRepository.findOne(userId);

        List<ProcessRole> userAppRoles = processRoleRepository.findByUserAndApplication(user, application);
        if(userAppRoles == null || userAppRoles.size()== 0){
            // user has no role on this application, so should not be able to write..
            log.error("FORBIDDEN TO SAVE");
            return null;
        }

        Response response = responseRepository.findByApplicationAndQuestion(application, question);
        if(response == null){
            response = new Response(null, LocalDateTime.now(), "", userAppRoles.get(0), question, application);
        }

        return response;
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

        List<ProcessRole> userAppRoles = processRoleRepository.findByUserAndApplication(user, application);

        Response response = this.getOrCreateResponse(applicationId, userId, questionId);
        if(response == null){
            log.error("FORBIDDEN TO SAVE");
            return new ResponseEntity<String>(headers, HttpStatus.FORBIDDEN);
        }

        if(!response.getValue().equals(value)) {
            response.setUpdateDate(LocalDateTime.now());
            response.setUpdatedBy(userAppRoles.get(0));
        }
        response.setValue(value);

        responseRepository.save(response);

        log.warn("Single question saved!");

        return new ResponseEntity<String>(headers, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/saveQuestionResponse/{responseId}/assessorFeedback", params="assessorUserId", method = RequestMethod.PUT, produces = "application/json")
    public @ResponseBody JsonStatusResponse saveQuestionResponseAssessorScore(@PathVariable("responseId") Long responseId,
                                                    @RequestParam("assessorUserId") Long assessorUserId,
                                                    @RequestParam("feedbackValue") Optional<String> feedbackValue,
                                                    @RequestParam("feedbackText") Optional<String> feedbackText,
                                                    HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

        Response response = responseRepository.findOne(responseId);

        Application application = response.getApplication();

        Either<JsonStatusResponse, JsonStatusResponse> result = withProcessRoleReturnJsonResponse(assessorUserId, UserRoleType.ASSESSOR, application.getId(), httpResponse, serviceLocator, assessorProcessRole -> {
            assessorService.updateAssessorFeedback(response.getId(), assessorProcessRole.getId(), feedbackValue, feedbackText);
            return right(JsonStatusResponse.ok());
        });

        return getLeftOrRight(result);
    }
}
