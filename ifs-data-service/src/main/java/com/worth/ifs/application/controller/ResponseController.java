package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.application.transactional.ResponseService;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.assessment.transactional.AssessorService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.CustomPermissionEvaluator;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;
import static com.worth.ifs.commons.rest.RestSuccesses.okRestSuccess;
import static com.worth.ifs.user.domain.UserRoleType.ASSESSOR;
import static com.worth.ifs.util.CollectionFunctions.onlyElement;
import static com.worth.ifs.util.EntityLookupCallbacks.getOrFail;

/**
 * ApplicationController exposes Application data and operations through a REST API.
 */
@RestController
@RequestMapping("/response")
public class ResponseController {

    private static final Error processRoleNotFoundError = notFoundError(ProcessRole.class, ASSESSOR.getName());
    private static final Error assessorRoleNotFoundError = notFoundError(Role.class, ASSESSOR.getName());

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

    QuestionController questionController = new QuestionController();

    @RequestMapping("/findResponsesByApplication/{applicationId}")
    public List<Response> findResponsesByApplication(@PathVariable("applicationId") final Long applicationId){
        return responseService.findResponsesByApplication(applicationId);
    }

    @RequestMapping(value = "/saveQuestionResponse/{responseId}/assessorFeedback", params="assessorUserId", method = RequestMethod.PUT, produces = "application/json")
    public RestResult<Void> saveQuestionResponseAssessorScore(@PathVariable("responseId") Long responseId,
                                                              @RequestParam("assessorUserId") Long assessorUserId,
                                                              @RequestParam("feedbackValue") Optional<String> feedbackValue,
                                                              @RequestParam("feedbackText") Optional<String> feedbackText) {

        return newRestHandler().andOnSuccess(okRestSuccess()).perform(() -> {

            Response response = responseRepository.findOne(responseId);
            Application application = response.getApplication();

            return getOrFail(() -> roleRepository.findByName(ASSESSOR.getName()), assessorRoleNotFoundError).
                   andOnSuccess(assessorRole -> getOrFail(() -> processRoleRepository.findByUserIdAndRoleAndApplicationId(assessorUserId, onlyElement(assessorRole), application.getId()), processRoleNotFoundError).
                   andOnSuccess(assessorProcessRole -> {

                       Feedback feedback = new Feedback().setResponseId(response.getId()).
                               setAssessorProcessRoleId(onlyElement(assessorProcessRole).getId()).
                               setValue(feedbackValue).
                               setText(feedbackText);
                       return assessorService.updateAssessorFeedback(feedback);
                   }
            ));
        });
    }

    @RequestMapping(value= "/assessorFeedback/{responseId}/{assessorProcessRoleId}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Feedback getFeedback(@PathVariable("responseId") Long responseId,
                                               @PathVariable("assessorProcessRoleId") Long assessorProcessRoleId){
        ServiceResult<Feedback> feedback = assessorService.getFeedback(new Feedback.Id().setAssessorProcessRoleId(assessorProcessRoleId).setResponseId(responseId));
        // TODO DW - how do we return a generic envelope to be consumed? failure is currently simply returning null.
        return feedback.handleSuccessOrFailure(l -> null, r -> r);
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
