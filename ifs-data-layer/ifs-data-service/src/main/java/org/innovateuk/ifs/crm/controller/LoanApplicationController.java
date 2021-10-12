package org.innovateuk.ifs.crm.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
@SecuredBySpring(value = "Controller", description = "TODO", securedType = LoanApplicationController.class)
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

    @PreAuthorize("permitAll()")
    @PostMapping(value = "/{applicationId}")
    public RestResult<Void> updateApplication(@PathVariable("applicationId") final Long applicationId,
                                              @RequestBody SilLoanApplicationStatus silApplication,
                                              BindingResult bindingResult, HttpServletRequest request) {
        return updateApplicationV1(applicationId, silApplication, bindingResult, request);
    }

    @PreAuthorize("permitAll()")
    @PostMapping(value = "/v1/{applicationId}")
    public RestResult<Void> updateApplicationV1(@PathVariable("applicationId") final Long applicationId,
                                                @Valid @RequestBody SilLoanApplicationStatus silStatus,
                                                BindingResult bindingResult, HttpServletRequest request) {

        if(bindingResult.hasErrors()) {
            return RestResult.restFailure(new Error(GENERAL_INVALID_ARGUMENT,
                    bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList())));
        }

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        if(user == null) {
            return RestResult.restFailure(HttpStatus.UNAUTHORIZED);
        } else {
            LOG.debug(String.format("test loan-app API user: %d %s", user.getId(), user.getName()));
            return getProcessRole(user, applicationId, silStatus);
        }
    }

    private RestResult<Void> getProcessRole(UserResource user, Long applicationId, SilLoanApplicationStatus silStatus) {
        return usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId).handleSuccessOrFailure(
                failure -> RestResult.restFailure(failure.getErrors(), HttpStatus.FORBIDDEN),
                processRole -> getCompetition(applicationId, silStatus, processRole.getId()));
    }

    private RestResult<Void> getCompetition(Long applicationId, SilLoanApplicationStatus silStatus, Long processRoleId) {
        return competitionService.getCompetitionByApplicationId(applicationId).handleSuccessOrFailure(
                failure -> RestResult.restFailure(failure.getErrors(), HttpStatus.BAD_REQUEST),
                competition -> {
                    if (!competition.isLoan()) {
                        LOG.debug(String.format("%d not a loan application", silStatus.getApplicationId()));
                        return RestResult.restFailure(new Error(CommonFailureKeys.GENERAL_FORBIDDEN));
                    }

                    return getQuestion(applicationId, silStatus, competition.getId(), processRoleId);
                });
    }

    private RestResult<Void> getQuestion(Long applicationId, SilLoanApplicationStatus silStatus, Long competitionId, Long processRoleId) {
        return questionService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, silStatus.getQuestionSetupType()).handleSuccessOrFailure(
                failure -> RestResult.restFailure(failure.getErrors(), HttpStatus.BAD_REQUEST),
                question -> {
                    QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(question.getId(), applicationId);
                    return markQuestionStatus(silStatus, ids, processRoleId).toPostResponse();
                });
    }

    private RestResult<Void> markQuestionStatus(SilLoanApplicationStatus silStatus, QuestionApplicationCompositeId ids, Long processRoleId)  {
        LOG.debug(String.format("test loan-app API: %d %s %d", ids.applicationId, silStatus.getQuestionSetupType() , processRoleId));
        return (silStatus.isCompletionStatus()) ?
                questionStatusService.markAsComplete(ids, processRoleId, silStatus.getCompletionDate()).toPostResponse() :
                questionStatusService.markAsInComplete(ids, processRoleId).toPostResponse();
    }

}
