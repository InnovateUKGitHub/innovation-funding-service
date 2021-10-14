package org.innovateuk.ifs.crm.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.transactional.QuestionStatusService;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.sil.crm.resource.SilLoanApplicationStatus;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_INVALID_ARGUMENT;

@RestController
@RequestMapping("/application-update")
@SecuredBySpring(value = "Controller", description = "LoanApplicationController", securedType = LoanApplicationController.class)
public class LoanApplicationController {

    private static final Log LOG = LogFactory.getLog(LoanApplicationController.class);

    @Autowired
    private UserAuthenticationService userAuthenticationService;
    @Autowired
    private UsersRolesService usersRolesService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionStatusService questionStatusService;
    @Autowired
    private ActivityLogService activityLogService;

    @PreAuthorize("permitAll()")
    @PatchMapping(value = "/{applicationId}")
    public RestResult<Void> updateApplication(@PathVariable("applicationId") final Long applicationId,
                                              @RequestBody SilLoanApplicationStatus silStatus,
                                              BindingResult bindingResult, HttpServletRequest request) {
        return updateApplicationV1(applicationId, silStatus, bindingResult, request);
    }

    @PreAuthorize("permitAll()")
    @PatchMapping(value = "/v1/{applicationId}")
    public RestResult<Void> updateApplicationV1(@PathVariable("applicationId") final Long applicationId,
                                                @Valid @RequestBody SilLoanApplicationStatus silStatus,
                                                BindingResult bindingResult, HttpServletRequest request) {

        if(bindingResult.hasErrors()) {
            LOG.error(String.format("application-update error: incorrect json for application %d", applicationId));
            return RestResult.restFailure(new Error(GENERAL_INVALID_ARGUMENT,
                    bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList())));
        }

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        if(user == null) {
            LOG.error(String.format("application-update error: user not found for application %d", applicationId));
            return RestResult.restFailure(HttpStatus.UNAUTHORIZED);
        } else {
            LOG.debug(String.format("application-update: user id=%d, name=%s", user.getId(), user.getName()));
            return getProcessRole(user, applicationId, silStatus);
        }
    }

    private RestResult<Void> getProcessRole(UserResource user, Long applicationId, SilLoanApplicationStatus silStatus) {
        return usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId).handleSuccessOrFailure(
                failure -> {
                    LOG.error(String.format("application-update error: process role not found using user %d, application %d", user.getId(), applicationId));
                    return RestResult.restFailure(failure.getErrors(), HttpStatus.FORBIDDEN);
                },
                processRole -> getCompetition(user, applicationId, silStatus, processRole.getId()));
    }

    private RestResult<Void> getCompetition(UserResource user, Long applicationId, SilLoanApplicationStatus silStatus, Long processRoleId) {
        return competitionService.getCompetitionByApplicationId(applicationId).handleSuccessOrFailure(
                failure -> RestResult.restFailure(failure.getErrors(), HttpStatus.BAD_REQUEST),
                competition -> {
                    if (!competition.isLoan()) {
                        LOG.error(String.format("application-update error: application %d is not an application for a loan competition", applicationId));
                        return RestResult.restFailure(new Error(CommonFailureKeys.GENERAL_FORBIDDEN));
                    }

                    return getQuestion(user, applicationId, silStatus, competition.getId(), processRoleId);
                });
    }

    private RestResult<Void> getQuestion(UserResource user, Long applicationId, SilLoanApplicationStatus silStatus, Long competitionId, Long processRoleId) {
        return questionService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, silStatus.getQuestionSetupType()).handleSuccessOrFailure(
                failure -> {
                        LOG.error(String.format("application-update error: question not found using application %d", applicationId));
                        return RestResult.restFailure(failure.getErrors(), HttpStatus.BAD_REQUEST);
                    },
                question -> {
                    QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(question.getId(), applicationId);
                    return markQuestionStatus(user, silStatus, ids, processRoleId);
                });
    }

    private RestResult<Void> markQuestionStatus(UserResource user, SilLoanApplicationStatus silStatus, QuestionApplicationCompositeId ids, Long processRoleId)  {
        LOG.debug(String.format("application-update: application=%d, question=%s, processrole=%d", ids.applicationId, silStatus.getQuestionSetupType() , processRoleId));
        if(silStatus.isCompletionStatus()) {
            return questionStatusService.markAsComplete(ids, processRoleId, silStatus.getCompletionDate()).handleSuccessOrFailure(
                    failure -> RestResult.restFailure(failure.getErrors(), HttpStatus.BAD_REQUEST),
                    success -> {
                        LOG.info(String.format("application-update: application %d marked complete", ids.applicationId));
                        activityLogService.recordActivityByApplicationId(ids.applicationId, user.getId(), ActivityType.APPLICATION_DETAILS_UPDATED);
                        return RestResult.restSuccess(HttpStatus.NO_CONTENT);
                }
            );
        } else {
            return questionStatusService.markAsInComplete(ids, processRoleId).handleSuccessOrFailure(
                    failure -> RestResult.restFailure(failure.getErrors(), HttpStatus.BAD_REQUEST),
                    success -> {
                        LOG.info(String.format("application-update: application %d marked incomplete", ids.applicationId));
                        activityLogService.recordActivityByApplicationId(ids.applicationId, user.getId(), ActivityType.APPLICATION_DETAILS_UPDATED);
                        return RestResult.restSuccess(HttpStatus.NO_CONTENT);
                    }
            );
        }
    }

}
