package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.overheads.OverheadFileSaver;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.collectValidationMessages;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.springframework.util.StringUtils.hasText;

/**
 * This Saver will handle save all sections that are related to the application.
 */
@Service
public class ApplicationSectionSaver extends AbstractApplicationSaver {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private FinanceHandler financeHandler;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private OverheadFileSaver overheadFileSaver;

    @Autowired
    private ApplicationSectionFinanceSaver financeSaver;

    public ValidationMessages saveApplicationForm(ApplicationResource application,
                                                  Long competitionId,
                                                  ApplicationForm form,
                                                  Long sectionId,
                                                  Long userId,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response, Boolean validFinanceTerms) {

        Long applicationId = application.getId();
        ProcessRoleResource processRole = processRoleService.findProcessRole(userId, applicationId);
        SectionResource selectedSection = sectionService.getById(sectionId);
        Map<String, String[]> params = request.getParameterMap();
        boolean ignoreEmpty = !isMarkSectionRequest(params);

        ValidationMessages errors = new ValidationMessages();

        if (isFundingRequest(params)) {
            financeSaver.handleRequestFundingRequests(params, applicationId, competitionId, processRole.getId());
        }

        if (!isMarkSectionAsIncompleteRequest(params)) {
            List<QuestionResource> questions = simpleMap(selectedSection.getQuestions(), questionService::getById);
            errors.addAll(saveQuestionResponses(request, questions, userId, processRole.getId(), applicationId, ignoreEmpty));

            Long organisationType = organisationService.getOrganisationType(userId, applicationId);
            ValidationMessages saveErrors = financeHandler.getFinanceFormHandler(organisationType).update(request, userId, applicationId, competitionId);

            if (overheadFileSaver.isOverheadFileRequest(request)) {
                errors.addAll(overheadFileSaver.handleOverheadFileRequest(request));
            } else {
                errors.addAll(saveErrors);
            }

            financeSaver.handleMarkAcademicFinancesAsNotRequired(organisationType, selectedSection, applicationId, competitionId, processRole.getId());
        }

        if (isMarkSectionRequest(params)) {
            errors.addAll(handleMarkSectionRequest(application, selectedSection, params, processRole, errors, validFinanceTerms));
            financeSaver.handleStateAid(params, application, form, selectedSection);
        }

        cookieFlashMessageFilter.setFlashMessage(response, "applicationSaved");

        return sortValidationMessages(errors);
    }



    private ValidationMessages handleMarkSectionRequest(ApplicationResource application, SectionResource selectedSection, Map<String, String[]> params,
                                                        ProcessRoleResource processRole, ValidationMessages errorsSoFar, Boolean validFinanceTerms) {
        ValidationMessages messages = new ValidationMessages();

        if (errorsSoFar.hasErrors()) {
            messages.addError(fieldError("formInput[cost]", "", MARKED_AS_COMPLETE_KEY));
        } else if (isMarkSectionAsIncompleteRequest(params) ||
                (isMarkSectionAsCompleteRequest(params) && validFinanceTerms)) {
            List<ValidationMessages> financeErrorsMark = markAllQuestionsInSection(application, selectedSection, processRole.getId(), params);

            if (collectValidationMessages(financeErrorsMark).hasErrors()) {
                messages.addError(fieldError("formInput[cost]", "", MARKED_AS_COMPLETE_KEY));
                messages.addAll(handleMarkSectionValidationMessages(financeErrorsMark));
            }
        }

        return messages;
    }

    private ValidationMessages handleMarkSectionValidationMessages(List<ValidationMessages> financeErrorsMark) {

        ValidationMessages toFieldErrors = new ValidationMessages();

        financeErrorsMark.forEach(validationMessage ->
                validationMessage.getErrors().stream()
                        .filter(Objects::nonNull)
                        .filter(e -> hasText(e.getErrorKey()))
                        .forEach(e -> {
                            if (validationMessage.getObjectName().equals("costItem")) {
                                if (hasText(e.getErrorKey())) {
                                    toFieldErrors.addError(fieldError("formInput[cost-" + validationMessage.getObjectId() + "-" + e.getFieldName() + "]", e));
                                } else {
                                    toFieldErrors.addError(fieldError(getFormCostInputKey(validationMessage.getObjectId()), e));
                                }
                            } else {
                                toFieldErrors.addError(fieldError(getFormInputKey(validationMessage.getObjectId()), e));
                            }
                        })
        );

        return toFieldErrors;
    }

    private List<ValidationMessages> markAllQuestionsInSection(ApplicationResource application,
                                                               SectionResource selectedSection,
                                                               Long processRoleId,
                                                               Map<String, String[]> params) {
        if (isMarkSectionAsCompleteRequest(params)) {
            return sectionService.markAsComplete(selectedSection.getId(), application.getId(), processRoleId);
        } else {
            sectionService.markAsInComplete(selectedSection.getId(), application.getId(), processRoleId);
        }

        return emptyList();
    }
}
