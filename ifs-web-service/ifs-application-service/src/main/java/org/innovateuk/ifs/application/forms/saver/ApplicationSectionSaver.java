package org.innovateuk.ifs.application.forms.saver;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.overheads.OverheadFileSaver;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
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

    private static final Log LOG = LogFactory.getLog(ApplicationSectionSaver.class);

    @Autowired
    private FinanceRowRestService financeRowRestService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private FinanceHandler financeHandler;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private OverheadFileSaver overheadFileSaver;

    @Autowired
    private ApplicationQuestionFileSaver fileSaver;

    @Autowired
    private ApplicationQuestionNonFileSaver nonFileSaver;

    public ValidationMessages saveApplicationForm(ApplicationResource application,
                                                  CompetitionResource competition,
                                                  ApplicationForm form,
                                                  Long sectionId,
                                                  UserResource user,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response, Boolean validFinanceTerms) {

        ProcessRoleResource processRole = processRoleService.findProcessRole(user.getId(), application.getId());
        SectionResource selectedSection = sectionService.getById(sectionId);
        Map<String, String[]> params = request.getParameterMap();

        boolean ignoreEmpty = (!params.containsKey(MARK_AS_COMPLETE)) && (!params.containsKey(MARK_SECTION_AS_COMPLETE));

        ValidationMessages errors = new ValidationMessages();

        if (isMarkSectionAsCompleteRequest(params)) {
            application.setStateAidAgreed(form.isStateAidAgreed());
        } else if (isMarkSectionAsIncompleteRequest(params) && selectedSection.getType() == SectionType.FINANCE) {
            application.setStateAidAgreed(Boolean.FALSE);
        }

        if (!isMarkQuestionAsIncompleteRequest(params)) {
            List<QuestionResource> questions = simpleMap(selectedSection.getQuestions(), questionService::getById);
            errors.addAll(saveQuestionResponses(request, questions, user.getId(), processRole.getId(), application.getId(), ignoreEmpty));
        }

        if (isFundingRequest(params)) {
            errors.addAll(handleRequestFundingRequests(params, application.getId(), user.getId(), competition.getId(), processRole.getId()));
        }

        if (!isMarkSectionAsIncompleteRequest(params)) {
            Long organisationType = organisationService.getOrganisationType(user.getId(), application.getId());
            ValidationMessages saveErrors = financeHandler.getFinanceFormHandler(organisationType).update(request, user.getId(), application.getId(), competition.getId());

            if (!overheadFileSaver.isOverheadFileRequest(request)) {
                errors.addAll(saveErrors);
            } else {
                errors.addAll(overheadFileSaver.handleOverheadFileRequest(request));
            }

            handleMarkAcademicFinancesAsNotRequired(organisationType, selectedSection, application.getId(), competition.getId(), processRole.getId());
        }

        if (isMarkSectionRequest(params)) {
            errors.addAll(handleMarkSectionRequest(application, sectionId, params, processRole, errors, validFinanceTerms));
        }

        cookieFlashMessageFilter.setFlashMessage(response, "applicationSaved");

        return sortValidationMessages(errors);
    }

    private ValidationMessages handleRequestFundingRequests(Map<String, String[]> params, Long applicationId, Long userId, Long competitionId, Long processRoleId) {
        if (isNotRequestingFundingRequest(params)) {
            return setRequestingFunding(NOT_REQUESTING_FUNDING, userId, applicationId, competitionId, processRoleId);
        } else {
            return setRequestingFunding(REQUESTING_FUNDING, userId, applicationId, competitionId, processRoleId);
        }
    }

    private ValidationMessages setRequestingFunding(String requestingFunding, Long userId, Long applicationId, Long competitionId, Long processRoleId) {
        ApplicationFinanceResource finance = financeService.getApplicationFinanceDetails(userId, applicationId);
        QuestionResource financeQuestion = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, FormInputType.FINANCE).getSuccessObjectOrThrowException();
        if (finance.getGrantClaim() != null) {
        }
        ValidationMessages errors = financeRowRestService.add(finance.getId(), financeQuestion.getId(), finance.getGrantClaim()).getOrElse(new ValidationMessages());

        if (!errors.hasErrors()) {
            SectionResource organisationSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.ORGANISATION_FINANCES).get(0);
            SectionResource fundingSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES).get(0);
            if (REQUESTING_FUNDING.equals(requestingFunding)) {
                sectionService.markAsInComplete(organisationSection.getId(), applicationId, processRoleId);
                sectionService.markAsInComplete(fundingSection.getId(), applicationId, processRoleId);
            } else if (NOT_REQUESTING_FUNDING.equals(requestingFunding)) {
                sectionService.markAsNotRequired(organisationSection.getId(), applicationId, processRoleId);
                sectionService.markAsNotRequired(fundingSection.getId(), applicationId, processRoleId);
            }
        }

        return errors;
    }

    private void handleMarkAcademicFinancesAsNotRequired(long organisationType, SectionResource selectedSection, long applicationId, long competitionId, long processRoleId) {
        if (SectionType.PROJECT_COST_FINANCES.equals(selectedSection.getType())
                && OrganisationTypeEnum.RESEARCH.getId().equals(organisationType)) {
            SectionResource organisationSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.ORGANISATION_FINANCES).get(0);
            SectionResource fundingSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES).get(0);
            sectionService.markAsNotRequired(organisationSection.getId(), applicationId, processRoleId);
            sectionService.markAsNotRequired(fundingSection.getId(), applicationId, processRoleId);
        }
    }

    private ValidationMessages handleMarkSectionRequest(ApplicationResource application, Long sectionId, Map<String, String[]> params,
                                                        ProcessRoleResource processRole, ValidationMessages errorsSoFar, Boolean validFinanceTerms) {
        ValidationMessages messages = new ValidationMessages();

        if (errorsSoFar.hasErrors()) {
            messages.addError(fieldError("formInput[cost]", "", MARKED_AS_COMPLETE_KEY));
        } else if (isMarkSectionAsIncompleteRequest(params) ||
                (isMarkSectionAsCompleteRequest(params) && validFinanceTerms)) {
            SectionResource selectedSection = sectionService.getById(sectionId);
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

    private ValidationMessages saveQuestionResponses(HttpServletRequest request,
                                                     List<QuestionResource> questions,
                                                     Long userId,
                                                     Long processRoleId,
                                                     Long applicationId,
                                                     boolean ignoreEmpty) {
        final Map<String, String[]> params = request.getParameterMap();

        ValidationMessages errors = new ValidationMessages();

        errors.addAll(nonFileSaver.saveNonFileUploadQuestions(questions, request, userId, applicationId, ignoreEmpty));
        errors.addAll(fileSaver.saveFileUploadQuestionsIfAny(questions, params, request, applicationId, processRoleId));

        return errors;
    }
}
