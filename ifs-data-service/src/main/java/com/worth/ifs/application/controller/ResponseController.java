package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.application.transactional.ResponseService;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.assessment.transactional.AssessorService;
import com.worth.ifs.security.CustomPermissionEvaluator;
import com.worth.ifs.transactional.ServiceFailure;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.util.Either.getLeftOrRight;
import static com.worth.ifs.util.Either.right;
import static com.worth.ifs.util.EntityLookupCallbacks.withProcessRoleReturnJsonResponse;

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
    CustomPermissionEvaluator permissionEvaluator;
    @Autowired
    ResponseService responseService;

    @Autowired
    ServiceLocator serviceLocator;

    QuestionController questionController = new QuestionController();

    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping("/findResponsesByApplication/{applicationId}")
    public List<Response> findResponsesByApplication(@PathVariable("applicationId") final Long applicationId){
        return responseService.findResponsesByApplication(applicationId);
    }

    private Response getOrCreateResponse(Long applicationId, Long userId,  Long questionId){
        Application application = applicationRepository.findOne(applicationId);
        Question question = questionRepository.findOne(questionId);
        User user = userRepository.findOne(userId);

        List<ProcessRole> userAppRoles = processRoleRepository.findByUserAndApplication(user, application);
        if(userAppRoles == null || userAppRoles.isEmpty()){
            // user has no role on this application, so should not be able to write..
            log.error("FORBIDDEN TO SAVE");
            return null;
        }

        Response response = responseRepository.findByApplicationAndQuestion(application, question);
        if(response == null){
            response = new Response(null, LocalDateTime.now(), userAppRoles.get(0), question, application);
        }

        return response;
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
            assessorService.updateAssessorFeedback(new Feedback().setResponseId(response.getId()).setAssessorProcessRoleId(assessorProcessRole.getId()).setValue(feedbackValue).setText(feedbackText));
            return right(JsonStatusResponse.ok());
        });

        return getLeftOrRight(result);
    }

    @RequestMapping(value= "/assessorFeedback/{responseId}/{assessorProcessRoleId}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Feedback getFeedback(@PathVariable("responseId") Long responseId,
                                               @PathVariable("assessorProcessRoleId") Long assessorProcessRoleId){
        Either<ServiceFailure, Feedback> feedback = assessorService.getFeedback(new Feedback.Id().setAssessorProcessRoleId(assessorProcessRoleId).setResponseId(responseId));
        // TODO how do we return a generic envelope to be consumed? failure is currently simply returning null.
        return feedback.mapLeftOrRight(l -> null, r -> r);
    }


    @RequestMapping(value= "assessorFeedback/permissions/{responseId}/{assessorProcessRoleId}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<String> permissions(@PathVariable("responseId") Long responseId,
                                                  @PathVariable("assessorProcessRoleId") Long assessorUserId){
        return permissionEvaluator.getPermissions(SecurityContextHolder.getContext().getAuthentication(),
                new Feedback.Id().setAssessorProcessRoleId(assessorUserId).setResponseId(responseId));
    }

    @RequestMapping(value= "assessorFeedback/permissions", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody List<String> permissions(@RequestBody Feedback feedback){
        return permissionEvaluator.getPermissions(SecurityContextHolder.getContext().getAuthentication(), feedback);
    }
}
