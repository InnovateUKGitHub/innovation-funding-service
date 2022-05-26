package org.innovateuk.ifs.application.validation;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.validator.*;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.transactional.HorizonWorkProgrammeService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Slf4j
@Component
public class ApplicationValidationUtil {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ApplicationValidatorService applicationValidatorService;

    @Autowired
    private ApplicationTeamMarkAsCompleteValidator applicationTeamMarkAsCompleteValidator;

    @Autowired
    private ApplicationDetailsMarkAsCompleteValidator applicationDetailsMarkAsCompleteValidator;

    @Autowired
    private ApplicationResearchMarkAsCompleteValidator applicationResearchMarkAsCompleteValidator;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private HorizonWorkProgrammeService horizonWorkProgrammeService;

    @Transactional
    public BindingResult validateResponse(FormInputResponseResource response, boolean ignoreEmpty) {
        return validateResponse(formInputResponseRepository.findById(response.getId()).get(), ignoreEmpty);
    }

    public BindingResult validateResponse(FormInputResponse response, boolean ignoreEmpty) {
        DataBinder binder = new DataBinder(response);
        if (response == null) {
            return binder.getBindingResult();
        }
        Set<FormValidator> validators = response.getFormInput().getFormValidators();

        // Get validators from the FormInput, and add to binder.
        validators.forEach(
                v ->
                {
                    Validator validator = null;
                    try {
                        // Sometimes we want to allow the user to enter a empty response. Then we can ignore the NotEmptyValidator .
                        if (!(ignoreEmpty &&
                                (v.getClazzName().equals(NotEmptyValidator.class.getName())))) {

                            validator = (Validator) context.getBean(Class.forName(v.getClazzName()));

                            binder.addValidators(validator);
                        }
                    } catch (Exception e) {
                        log.error("Could not find validator class: " + v.getClazzName());
                        log.error("Exception message: " + e.getMessage());
                        log.error(e.getMessage(), e);
                    }
                }
        );
        binder.validate();
        return binder.getBindingResult();
    }

    public BindingResult addValidation(Application application, Validator validator) {
        DataBinder binder = new DataBinder(application);
        binder.addValidators(validator);
        binder.validate();
        return binder.getBindingResult();
    }

    public ValidationMessages isSectionValid(Long markedAsCompleteById, Section section, Application application) {
        ValidationMessages validationMessages = new ValidationMessages();
        for (Question question : section.fetchAllQuestionsAndChildQuestions()) {
            if (question.getMarkAsCompletedEnabled()) {
                validationMessages.addAll(isQuestionValid(question, application, markedAsCompleteById));
            }
        }
        List<FinanceRowType> competitionFinanceTypes = section.getCompetition().getFinanceRowTypes();
        if (SectionType.PROJECT_COST_FINANCES == section.getType()) {
            boolean isHecpCompetition = application.getCompetition().isHorizonEuropeGuarantee();
            if (isHecpCompetition) {
                ImmutableSet.of(LABOUR, OVERHEADS, EQUIPMENT, OTHER_GOODS, SUBCONTRACTING_COSTS, TRAVEL, OTHER_COSTS)
                        .stream()
                        .filter(competitionFinanceTypes::contains)
                        .forEach(type -> validationMessages.addAll(applicationValidatorService.validateCostItem(application.getId(), type, markedAsCompleteById)));
            } else {
                ImmutableSet.of(LABOUR, OVERHEADS, MATERIALS, CAPITAL_USAGE, SUBCONTRACTING_COSTS, TRAVEL, OTHER_COSTS)
                        .stream()
                        .filter(competitionFinanceTypes::contains)
                        .forEach(type -> validationMessages.addAll(applicationValidatorService.validateCostItem(application.getId(), type, markedAsCompleteById)));
            }
            validationMessages.addAll(applicationValidatorService.validateAcademicUpload(application, markedAsCompleteById));
        } else if (SectionType.FUNDING_FINANCES == section.getType()) {
            ImmutableSet.of(FINANCE, OTHER_FUNDING)
                    .stream()
                    .filter(competitionFinanceTypes::contains)
                    .forEach(type -> validationMessages.addAll(applicationValidatorService.validateCostItem(application.getId(), type, markedAsCompleteById)));
        } else if (SectionType.FEC_COSTS_FINANCES == section.getType()) {
            validationMessages.addAll(applicationValidatorService.validateFECCertificateUpload(application, markedAsCompleteById));
        }
        return validationMessages;
    }


    public List<ValidationMessages> isQuestionValid(Question question, Application application, Long markedAsCompleteById) {
        List<ValidationMessages> validationMessages = new ArrayList<>();
        List<FormInput> formInputs = simpleFilter(question.getFormInputs(), formInput -> formInput.getActive() && APPLICATION.equals(formInput.getScope()));
        List<ApplicationHorizonWorkProgrammeResource> workProgrammeResource = horizonWorkProgrammeService.findSelectedForApplication(application.getId()).getSuccess();
        if (question.hasMultipleStatuses()) {
            for (FormInput formInput : formInputs) {
                validationMessages.addAll(isMultipleStatusFormInputValid(application, markedAsCompleteById, formInput));
            }
        } else if (question.getQuestionSetupType() == QuestionSetupType.APPLICATION_TEAM) {
            validationMessages.addAll(isApplicationTeamValid(application, question));
        } else if (question.getQuestionSetupType() == QuestionSetupType.APPLICATION_DETAILS) {
            validationMessages.addAll(isApplicationDetailsValid(application, question));
        } else if (question.getQuestionSetupType() == QuestionSetupType.RESEARCH_CATEGORY) {
            validationMessages.addAll(isResearchCategoryValid(application, question));
        } else if (question.getQuestionSetupType() == QuestionSetupType.HORIZON_WORK_PROGRAMME && (workProgrammeResource.size() == 0)) {
            validationMessages.addAll(isWorkProgrammeValid(application.getId()));
        } else {
            for (FormInput formInput : formInputs) {
                validationMessages.addAll(isSingleStatusFormInputValid(application, formInput));
            }
        }
        return validationMessages;
    }

    private List<ValidationMessages> isApplicationDetailsValid(Application application, Question question) {
        List<ValidationMessages> validationMessages = new ArrayList<>();

        BindingResult bindingResult = addValidation(application, applicationDetailsMarkAsCompleteValidator);
        if (bindingResult.hasErrors()) {
            validationMessages.add(new ValidationMessages(question.getId(), bindingResult));
        }
        return validationMessages;
    }

    private List<ValidationMessages> isResearchCategoryValid(Application application, Question question) {
        List<ValidationMessages> validationMessages = new ArrayList<>();

        BindingResult bindingResult = addValidation(application, applicationResearchMarkAsCompleteValidator);
        if (bindingResult.hasErrors()) {
            validationMessages.add(new ValidationMessages(question.getId(), bindingResult));
        }
        return validationMessages;
    }

    private List<ValidationMessages> isApplicationTeamValid(Application application, Question question) {
        List<ValidationMessages> validationMessages = new ArrayList<>();

        BindingResult bindingResult = addValidation(application, applicationTeamMarkAsCompleteValidator);
        if (bindingResult.hasErrors()) {
            validationMessages.add(new ValidationMessages(question.getId(), bindingResult));
        }
        return validationMessages;
    }

    public List<ValidationMessages> isWorkProgrammeValid(long applicationId) {
        List<ValidationMessages> validationMessages = new ArrayList<>();
        List<BindingResult> bindingResults = validateWorkProgrammeResponse(applicationId);
        for (BindingResult bindingResult : bindingResults) {
            if (bindingResult.hasErrors()) {
                validationMessages.add(new ValidationMessages(bindingResult));
            }
        }
        return validationMessages;
    }

    public List<BindingResult> validateWorkProgrammeResponse(long applicationId) {
        List<BindingResult> results = new ArrayList<>();
        List<ApplicationHorizonWorkProgrammeResource> workProgrammeResource = horizonWorkProgrammeService.findSelectedForApplication(applicationId).getSuccess();
        DataBinder binder = new DataBinder(workProgrammeResource);

        if (workProgrammeResource.isEmpty()) {
            results.add(binder.getBindingResult());
        }
        return results;
    }

    public List<ValidationMessages> isApplicationDetailsValid(Application application) {
        List<ValidationMessages> validationMessages = new ArrayList<>();

        BindingResult bindingResult = addValidation(application, applicationDetailsMarkAsCompleteValidator);
        if (bindingResult.hasErrors()) {
            validationMessages.add(new ValidationMessages(bindingResult));
        }
        return validationMessages;
    }

    private List<ValidationMessages> isSingleStatusFormInputValid(Application application, FormInput formInput) {
        List<ValidationMessages> validationMessages = new ArrayList<>();
        List<BindingResult> bindingResults = applicationValidatorService.validateFormInputResponse(application.getId(), formInput.getId());
        for (BindingResult bindingResult : bindingResults) {
            if (bindingResult.hasErrors()) {
                validationMessages.add(new ValidationMessages(formInput.getId(), bindingResult));
            }
        }
        return validationMessages;
    }

    private List<ValidationMessages> isMultipleStatusFormInputValid(Application application, Long markedAsCompleteById, FormInput formInput) {
        List<ValidationMessages> validationMessages = new ArrayList<>();
        if (formInput.getFormValidators().isEmpty()) {
            // no validator? question is valid!
        } else {
            ValidationMessages validationResult = applicationValidatorService.validateFormInputResponse(application, formInput.getId(), markedAsCompleteById);

            if (validationResult.hasErrors()) {
                validationMessages.add(new ValidationMessages(formInput.getId(), validationResult));
            }
        }
        return validationMessages;
    }

}
