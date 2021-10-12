package org.innovateuk.ifs.crm.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.transactional.QuestionStatusService;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;

import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.sil.crm.resource.SilLoanApplicationStatus;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UserService;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/application-update")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = LoanApplicationController.class)
public class LoanApplicationController {

    private static final Log LOG = LogFactory.getLog(LoanApplicationController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UsersRolesService usersRolesService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionStatusService questionStatusService;

    private final DateTimeFormatter SIL_DATETIME_FORMAT = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());

    @PreAuthorize("permitAll()")
    @PostMapping(value = "/{applicationId}")
    public RestResult<Void> updateApplication(@PathVariable("applicationId") final Long applicationId,
                                              @RequestBody SilLoanApplicationStatus silApplication, UserResource loggedInUser) {
        return updateApplicationV1(applicationId, silApplication, loggedInUser);
    }

    @PreAuthorize("permitAll()")
    @PostMapping(value = "/v1/{applicationId}")
    public RestResult<Void> updateApplicationV1(@PathVariable("applicationId") final Long applicationId,
                                              @RequestBody SilLoanApplicationStatus silStatus, UserResource loggedInUser) {

        LOG.info("test loan-app API logged in user: " + (loggedInUser == null ? "null" : loggedInUser.getEmail()));

        return userService.findByUid(silStatus.getCompletedBy()).handleSuccessOrFailure(
                failure -> RestResult.restFailure(failure.getErrors(), HttpStatus.UNAUTHORIZED),
                user -> {
                    LOG.info(String.format("test loan-app API user: %d %s", user.getId(), user.getName()));
                    return getProcessRole(user, silStatus);
                });
    }


    private RestResult<Void> getProcessRole(UserResource user, SilLoanApplicationStatus silStatus) {
        return usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), silStatus.getApplicationId()).handleSuccessOrFailure(
                failure -> RestResult.restFailure(failure.getErrors(), HttpStatus.FORBIDDEN),
                processRole -> {

                    LOG.info(String.format("test loan-app API process role: %d %s", processRole.getId(), processRole.getRole()));
                    if(!user.hasAnyRoles(Role.IFS_ADMINISTRATOR, Role.COMP_ADMIN) &&
                            ProcessRoleType.LEADAPPLICANT != processRole.getRole()) {
                        LOG.info(String.format("user is not admin or lead applicant"));
                        return RestResult.restFailure(new Error(CommonFailureKeys.GENERAL_FORBIDDEN));
                    }

                    return getCompetition(silStatus, processRole.getId());
                });
    }

    private RestResult<Void> getCompetition(SilLoanApplicationStatus silStatus, Long processRoleId) {
        return competitionService.getCompetitionByApplicationId(silStatus.getApplicationId()).handleSuccessOrFailure(
                failure -> RestResult.restFailure(failure.getErrors(), HttpStatus.BAD_REQUEST),
                competition -> {
                    if (!competition.isLoan()) {
                        LOG.info(String.format("%d not a loan application", silStatus.getApplicationId()));
                        return RestResult.restFailure(new Error(CommonFailureKeys.GENERAL_FORBIDDEN));
                    }

                    return getQuestion(silStatus, competition.getId(), processRoleId);
                });
    }

    private RestResult<Void> getQuestion(SilLoanApplicationStatus silStatus, Long competitionId, Long processRoleId) {
        return questionService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, silStatus.getQuestionSetupType()).handleSuccessOrFailure(
                failure -> RestResult.restFailure(failure.getErrors(), HttpStatus.BAD_REQUEST),
                question -> {
                    QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(question.getId(), silStatus.getApplicationId());
                    return markQuestionStatus(silStatus, ids, processRoleId).toPostResponse();
                });
    }

    private RestResult<Void> markQuestionStatus(SilLoanApplicationStatus silStatus, QuestionApplicationCompositeId ids, Long processRoleId)  {
        LOG.info(String.format("test loan-app API: %d %s %d", ids.applicationId, silStatus.getQuestionSetupType() , processRoleId));
        return "COMPLETE".equalsIgnoreCase(silStatus.getCompletionStatus()) ?
                questionStatusService.markAsComplete(ids, processRoleId, ZonedDateTime.parse(silStatus.getCompletionDate(), SIL_DATETIME_FORMAT)).toPostResponse() :
                questionStatusService.markAsInComplete(ids, processRoleId).toPostResponse();
    }

}
