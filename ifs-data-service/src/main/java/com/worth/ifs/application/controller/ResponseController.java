package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.application.transactional.ResponseService;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.assessment.transactional.AssessorService;
import com.worth.ifs.commons.controller.ServiceFailureToJsonResponseHandler;
import com.worth.ifs.commons.controller.SimpleServiceFailureToJsonResponseHandler;
import com.worth.ifs.security.CustomPermissionEvaluator;
import com.worth.ifs.transactional.ServiceLocator;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.util.JsonStatusResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.commons.controller.ControllerErrorHandlingUtil.handleServiceFailure;
import static com.worth.ifs.transactional.BaseTransactionalService.Failures.*;
import static com.worth.ifs.transactional.ServiceResult.success;
import static com.worth.ifs.user.domain.UserRoleType.ASSESSOR;
import static com.worth.ifs.util.EntityLookupCallbacks.withProcessRoleReturnJsonResponse;
import static com.worth.ifs.util.JsonStatusResponse.internalServerError;
import static com.worth.ifs.util.JsonStatusResponse.notFound;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * ApplicationController exposes Application data and operations through a REST API.
 */
@RestController
@RequestMapping("/response")
public class ResponseController {

    private List<ServiceFailureToJsonResponseHandler> serviceFailureHandlers = asList(

        new SimpleServiceFailureToJsonResponseHandler(singletonList(ROLE_NOT_FOUND), (serviceFailure, response) -> notFound("Unable to find file", response)),
        new SimpleServiceFailureToJsonResponseHandler(singletonList(APPLICATION_NOT_FOUND), (serviceFailure, response) -> notFound("Unable to find Application", response)),
        new SimpleServiceFailureToJsonResponseHandler(singletonList(PROCESS_ROLE_NOT_FOUND), (serviceFailure, response) -> notFound("Unable to find Process Role", response))
    );

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

    @RequestMapping(value = "/saveQuestionResponse/{responseId}/assessorFeedback", params="assessorUserId", method = RequestMethod.PUT, produces = "application/json")
    public @ResponseBody JsonStatusResponse saveQuestionResponseAssessorScore(@PathVariable("responseId") Long responseId,
                                                    @RequestParam("assessorUserId") Long assessorUserId,
                                                    @RequestParam("feedbackValue") Optional<String> feedbackValue,
                                                    @RequestParam("feedbackText") Optional<String> feedbackText,
                                                    HttpServletResponse httpResponse) {

        Response response = responseRepository.findOne(responseId);

        Application application = response.getApplication();

        ServiceResult<JsonStatusResponse> result =
                withProcessRoleReturnJsonResponse(assessorUserId, ASSESSOR, application.getId(), serviceLocator, assessorProcessRole -> {

            assessorService.updateAssessorFeedback(new Feedback().setResponseId(response.getId()).setAssessorProcessRoleId(assessorProcessRole.getId()).setValue(feedbackValue).setText(feedbackText));
            return success(JsonStatusResponse.ok());
        });

        return result.mapLeftOrRight(
                failure -> handleServiceFailure(failure, serviceFailureHandlers, httpResponse).orElseGet(() -> internalServerError("Unable to save question response", httpResponse)),
                success -> success);
    }

    @RequestMapping(value= "/assessorFeedback/{responseId}/{assessorProcessRoleId}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Feedback getFeedback(@PathVariable("responseId") Long responseId,
                                               @PathVariable("assessorProcessRoleId") Long assessorProcessRoleId){
        ServiceResult<Feedback> feedback = assessorService.getFeedback(new Feedback.Id().setAssessorProcessRoleId(assessorProcessRoleId).setResponseId(responseId));
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
