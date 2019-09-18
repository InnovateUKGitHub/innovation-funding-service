package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.error.ValidationMessages.collectValidationMessages;

/**
 * This Saver will handle save all questions that are related to the application.
 */
@Service
public class ApplicationQuestionSaver extends AbstractApplicationSaver {

    private static final String MARKED_AS_COMPLETE_INVALID_DATA_KEY = "mark.as.complete.invalid.data.exists";

    private UserRestService userRestService;
    private ApplicationService applicationService;
    private UserService userService;
    private QuestionService questionService;
    private QuestionRestService questionRestService;
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    private ApplicationQuestionFileSaver fileSaver;
    private ApplicationQuestionNonFileSaver nonFileSaver;

    public ApplicationQuestionSaver(UserRestService userRestService,
                                    ApplicationService applicationService,
                                    UserService userService,
                                    QuestionService questionService,
                                    QuestionRestService questionRestService,
                                    CookieFlashMessageFilter cookieFlashMessageFilter,
                                    ApplicationQuestionFileSaver fileSaver,
                                    ApplicationQuestionNonFileSaver nonFileSaver) {
        this.userRestService = userRestService;
        this.applicationService = applicationService;
        this.userService = userService;
        this.questionService = questionService;
        this.questionRestService = questionRestService;
        this.cookieFlashMessageFilter = cookieFlashMessageFilter;
        this.fileSaver = fileSaver;
        this.nonFileSaver = nonFileSaver;
    }

    public ValidationMessages saveApplicationForm(Long applicationId,
                                                  ApplicationForm form,
                                                  Long questionId,
                                                  Long userId,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  Optional<Boolean> markAsCompleteRequest) {
        final ApplicationResource application = applicationService.getById(applicationId);

        final List<QuestionResource> questionList = singletonList(questionRestService.findById(questionId).getSuccess());
        final Map<String, String[]> params = request.getParameterMap();
        final ValidationMessages errors = new ValidationMessages();
        final boolean ignoreEmpty = !params.containsKey(MARK_AS_COMPLETE);

        ProcessRoleResource processRole = userRestService.findProcessRole(userId, application.getId()).getSuccess();

        if (!isMarkQuestionAsIncompleteRequest(params)) {
            errors.addAll(nonFileSaver.saveNonFileUploadQuestions(questionList, request, userId, applicationId, ignoreEmpty));
            errors.addAll(fileSaver.saveFileUploadQuestionsIfAny(questionList, request.getParameterMap(), request, applicationId, processRole.getId()));
        }

        if (userService.isLeadApplicant(userId, application)) {
            applicationService.save(application);
        }

        boolean isMarkAsCompleteRequest = markAsCompleteRequest.orElse(false);
        if (isMarkAsCompleteRequest || (isMarkQuestionRequest(params) && !errors.hasErrors())) {
            if (isMarkQuestionAsIncompleteRequest(request.getParameterMap())) {

                questionService.markAsIncomplete(questionId, applicationId, processRole.getId());
            } else {
                errors.addAll(handleLeadOnlyMarkCompletedRequest(application.getId(), questionId, processRole.getId(), errors));
            }
        }

        cookieFlashMessageFilter.setFlashMessage(response, "applicationSaved");
        return sortValidationMessages(errors);
    }

    private ValidationMessages handleLeadOnlyMarkCompletedRequest(long applicationId,
                                                                  long questionId,
                                                                  long processRoleId,
                                                                  ValidationMessages errorsSoFar) {
        List<ValidationMessages> applicationMessages = questionService.markAsComplete(questionId, applicationId,
                processRoleId);

        if (errorsSoFar.hasFieldErrors(String.valueOf(questionId))) {
            applicationMessages.add(new ValidationMessages(
                    fieldError(Long.toString(questionId), "", MARKED_AS_COMPLETE_INVALID_DATA_KEY)));
        }

        ValidationMessages combinedMessages = collectValidationMessages(applicationMessages);
        if (combinedMessages.hasErrors()) {
            questionService.markAsIncomplete(questionId, applicationId, processRoleId);
        }

        return combinedMessages;
    }
}
