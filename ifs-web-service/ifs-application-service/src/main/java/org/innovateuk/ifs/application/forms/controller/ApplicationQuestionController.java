package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

import static org.innovateuk.ifs.application.ApplicationUrlHelper.getQuestionUrl;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;

/**
 * This controller will handle all question requests that are related to the application form.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ApplicationQuestionController.class)
@PreAuthorize("hasAnyAuthority('applicant', 'project_finance', 'ifs_administrator', 'comp_admin', 'support', 'innovation_lead', 'stakeholder', 'assessor', 'monitoring_officer')")
public class ApplicationQuestionController {

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @GetMapping(value = {QUESTION_URL + "{" + QUESTION_ID + "}", QUESTION_URL + "edit/{" + QUESTION_ID + "}"})
    public String showQuestion(
            @PathVariable long applicationId,
            @PathVariable long questionId,
            @RequestParam("show-errors") final Optional<Boolean> showErrors,
            UserResource user) {
        verifyApplicationIdExistsAndPermission(applicationId);
        QuestionResource questionResource = questionRestService.findById(questionId).getSuccess();
        QuestionSetupType questionType = questionResource.getQuestionSetupType();
        Optional<String> questionUrl = getQuestionUrl(questionType, questionId, applicationId);
        if (questionUrl.isPresent()) {
            return "redirect:" + questionUrl.get() + (showErrors.isPresent() ? "?show-errors=true" : "");
        }
        throw new IFSRuntimeException("Unknown question type" + questionType.name());
    }

    private void verifyApplicationIdExistsAndPermission(long applicationId) {
        applicationRestService.getApplicationById(applicationId).getSuccess();
    }
}