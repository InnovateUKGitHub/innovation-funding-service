package com.worth.ifs.application.controller;

import com.worth.ifs.application.resource.ResponseResource;
import com.worth.ifs.application.transactional.ResponseService;
import com.worth.ifs.assessment.resource.Feedback;
import com.worth.ifs.assessment.transactional.AssessorService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.security.CustomPermissionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * ApplicationController exposes Application data and operations through a REST API.
 */
@RestController
@RequestMapping("/response")
public class ResponseController {

    @Autowired
    private AssessorService assessorService;

    @Autowired
    private CustomPermissionEvaluator permissionEvaluator;

    @Autowired
    private ResponseService responseService;

    @RequestMapping("/findResponsesByApplication/{applicationId}")
    public RestResult<List<ResponseResource>> findResponsesByApplication(@PathVariable("applicationId") final Long applicationId){
        return responseService.findResponseResourcesByApplication(applicationId).toGetResponse();
    }

    @RequestMapping(value = "/saveQuestionResponse/{responseId}/assessorFeedback", params="assessorUserId", method = RequestMethod.PUT, produces = "application/json")
    public RestResult<Void> saveQuestionResponseAssessorScore(@PathVariable("responseId") Long responseId,
                                                              @RequestParam("assessorUserId") Long assessorUserId,
                                                              @RequestParam("feedbackValue") Optional<String> feedbackValue,
                                                              @RequestParam("feedbackText") Optional<String> feedbackText) {

        return assessorService.updateAssessorFeedback(new Feedback.Id(responseId, assessorUserId), feedbackValue, feedbackText).toPutResponse();
    }

    @RequestMapping(value= "/assessorFeedback/{responseId}/{assessorProcessRoleId}", method = RequestMethod.GET, produces = "application/json")
    public RestResult<Feedback> getFeedback(@PathVariable("responseId") Long responseId,
                                            @PathVariable("assessorProcessRoleId") Long assessorProcessRoleId){

        return assessorService.getFeedback(new Feedback.Id().setAssessorUserId(assessorProcessRoleId).setResponseId(responseId)).toGetResponse();
    }


    @RequestMapping(value= "assessorFeedback/permissions/{responseId}/{assessorProcessRoleId}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<String> permissions(@PathVariable("responseId") Long responseId,
                                                  @PathVariable("assessorProcessRoleId") Long assessorUserId){
        return permissionEvaluator.getPermissions(SecurityContextHolder.getContext().getAuthentication(),
                new Feedback.Id().setAssessorUserId(assessorUserId).setResponseId(responseId));
    }

    @RequestMapping(value= "assessorFeedback/permissions", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody List<String> permissions(@RequestBody Feedback feedback){
        return permissionEvaluator.getPermissions(SecurityContextHolder.getContext().getAuthentication(), feedback);
    }
}
