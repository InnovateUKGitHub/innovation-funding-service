package org.innovateuk.ifs.application.forms.saver;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.collectValidationMessages;

/**
 * This Saver will handle save all questions that are related to the application.
 */
@Service
public class ApplicationQuestionSaver extends AbstractApplicationSaver {

    private static final Log LOG = LogFactory.getLog(ApplicationQuestionSaver.class);
    private static final String MARKED_AS_COMPLETE_INVALID_DATA_KEY = "mark.as.complete.invalid.data.exists";

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private ApplicationQuestionApplicationDetailsSaver detailsSaver;

    public ValidationMessages saveApplicationForm(Long applicationId,
                                                  ApplicationForm form,
                                                  Long questionId,
                                                  Long userId,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  Boolean hasErrorsInBindingResult,
                                                  Optional<Boolean> markAsCompleteRequest) {
        final ApplicationResource application = applicationService.getById(applicationId);
        final List<QuestionResource> questionList = singletonList(questionService.getById(questionId));
        final Map<String, String[]> params = request.getParameterMap();
        final ValidationMessages errors = new ValidationMessages();
        final boolean ignoreEmpty = !params.containsKey(MARK_AS_COMPLETE);

        ProcessRoleResource processRole = processRoleService.findProcessRole(userId, application.getId());

        if (!isMarkQuestionAsIncompleteRequest(params)) {
            errors.addAll(saveQuestionResponses(request, questionList, userId, processRole.getId(), application.getId(), ignoreEmpty));
        }

        detailsSaver.setApplicationDetails(application, form.getApplication());

        if (userService.isLeadApplicant(userId, application)) {
            applicationService.save(application);
        }

        if ((markAsCompleteRequest.isPresent() && markAsCompleteRequest.get())
                || (isMarkQuestionRequest(params) && hasNoErrors(errors, hasErrorsInBindingResult))) {
            errors.addAll(handleApplicationDetailsMarkCompletedRequest(application.getId(), questionId, processRole.getId(), errors, request));
        }

        cookieFlashMessageFilter.setFlashMessage(response, "applicationSaved");

        return sortValidationMessages(errors);
    }

    private ValidationMessages handleApplicationDetailsMarkCompletedRequest(Long applicationId, Long questionId, Long processRoleId, ValidationMessages errorsSoFar, HttpServletRequest request) {
        ValidationMessages messages = new ValidationMessages();
        List<ValidationMessages> applicationMessages = markApplicationQuestions(applicationId, questionId, processRoleId, errorsSoFar, request);

        if (collectValidationMessages(applicationMessages).hasErrors()) {
            messages.addAll(detailsSaver.handleApplicationDetailsValidationMessages(applicationMessages));
        }

        return messages;
    }

    private boolean hasNoErrors(ValidationMessages errorsSoFar, Boolean hasErrorsInBindingResult) {
        return !errorsSoFar.hasErrors() && !hasErrorsInBindingResult;
    }


    private List<ValidationMessages> markApplicationQuestions(Long applicationId, Long questionId, Long processRoleId, ValidationMessages errorsSoFar, HttpServletRequest request) {

        if (processRoleId == null) {
            return emptyList();
        }

        if (isMarkQuestionAsIncompleteRequest(request.getParameterMap())) {
            questionService.markAsIncomplete(questionId, applicationId, processRoleId);

            return emptyList();
        } else {
            return markQuestionAsComplete(applicationId, questionId, processRoleId, errorsSoFar);
        }
    }

    private List<ValidationMessages> markQuestionAsComplete(Long applicationId, Long questionId, Long processRoleId, ValidationMessages errorsSoFar) {
        List<ValidationMessages> markAsCompleteErrors = questionService.markAsComplete(questionId, applicationId, processRoleId);

        if (!markAsCompleteErrors.isEmpty()) {
            questionService.markAsIncomplete(questionId, applicationId, processRoleId);
        }

        if (errorsSoFar.hasFieldErrors(String.valueOf(questionId))) {
            markAsCompleteErrors.add(new ValidationMessages(fieldError(questionId + "", "", MARKED_AS_COMPLETE_INVALID_DATA_KEY)));
        }

        return markAsCompleteErrors;
    }
}
