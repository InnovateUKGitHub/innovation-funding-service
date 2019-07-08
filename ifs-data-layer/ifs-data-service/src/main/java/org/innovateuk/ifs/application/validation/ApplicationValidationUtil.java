package org.innovateuk.ifs.application.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.validator.ApplicationResearchMarkAsCompleteValidator;
import org.innovateuk.ifs.application.validator.ApplicationTeamMarkAsCompleteValidator;
import org.innovateuk.ifs.application.validator.NotEmptyValidator;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.Error.globalError;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Component
public class ApplicationValidationUtil {
    private final static Log LOG = LogFactory.getLog(ApplicationValidationUtil.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ApplicationValidatorService applicationValidatorService;

    @Autowired
    private ApplicationTeamMarkAsCompleteValidator applicationTeamMarkAsCompleteValidator;

    @Autowired
    private ApplicationResearchMarkAsCompleteValidator applicationResearchMarkAsCompleteValidator;

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
                        LOG.error("Could not find validator class: " + v.getClazzName());
                        LOG.error("Exception message: " + e.getMessage());
                        LOG.error(e);
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

    public List<ValidationMessages> isSectionValid(Long markedAsCompleteById, Section section, Application application) {
        List<ValidationMessages> validationMessages = new ArrayList<>();
        for (Question question : section.fetchAllQuestionsAndChildQuestions()) {
            if (question.getMarkAsCompletedEnabled()) {
                validationMessages.addAll(isQuestionValid(question, application, markedAsCompleteById));
            }
        }
        return validationMessages;
    }

    public List<ValidationMessages> isQuestionValid(Question question, Application application, Long markedAsCompleteById) {
        List<ValidationMessages> validationMessages = new ArrayList<>();
        List<FormInput> formInputs = simpleFilter(question.getFormInputs(), formInput -> formInput.getActive() && APPLICATION.equals(formInput.getScope()));
        if (question.hasMultipleStatuses()) {
            for (FormInput formInput : formInputs) {
                validationMessages.addAll(isFormInputValid(question, application, markedAsCompleteById, formInput));
            }

        } else if (question.getQuestionSetupType() == QuestionSetupType.APPLICATION_TEAM) {
            validationMessages.addAll(isApplicationTeamValid(application, question));
        } else if (question.getQuestionSetupType() == QuestionSetupType.RESEARCH_CATEGORY) {
            validationMessages.addAll(isResearchCategoryValid(application, question));
        } else {
            for (FormInput formInput : formInputs) {
                validationMessages.addAll(isFormInputValid(application, formInput));
            }
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

    private List<ValidationMessages> isFormInputValid(Application application, FormInput formInput) {
        List<ValidationMessages> validationMessages = new ArrayList<>();
        List<BindingResult> bindingResults = applicationValidatorService.validateFormInputResponse(application.getId(), formInput.getId());
        for (BindingResult bindingResult : bindingResults) {
            if (bindingResult.hasErrors()) {
                validationMessages.add(new ValidationMessages(formInput.getId(), bindingResult));
            }
        }
        return validationMessages;
    }

    private List<ValidationMessages> isFormInputValid(Question question, Application application, Long markedAsCompleteById, FormInput formInput) {
        List<ValidationMessages> validationMessages = new ArrayList<>();
        if (formInput.getFormValidators().isEmpty() && !hasValidator(formInput)) {
            // no validator? question is valid!
        } else {
            ValidationMessages validationResult = applicationValidatorService.validateFormInputResponse(application, formInput.getId(), markedAsCompleteById);

            if (validationResult.hasErrors()) {
                validationMessages.add(new ValidationMessages(formInput.getId(), validationResult));
            }
        }

        validationCostItem(question, application, markedAsCompleteById, formInput, validationMessages);
        return validationMessages;
    }

    private boolean hasValidator(FormInput formInput) {
        return formInput.getType().equals(FormInputType.FINANCE_UPLOAD);
    }

    private void validationCostItem(Question question, Application application, Long markedAsCompleteById, FormInput formInput, List<ValidationMessages> validationMessages) {
        try {
            FinanceRowType type = FinanceRowType.fromType(formInput.getType()); // this checks if formInput is CostType related.
            validationMessages.addAll(applicationValidatorService.validateCostItem(application.getId(), type, markedAsCompleteById));
        } catch (IllegalArgumentException e) {
            // not a costtype, which is fine...
            LOG.trace("input type not a cost type", e);
        }
    }

    public List<ValidationMessages> validateCostItem(List<FinanceRowItem> costItems) {
        if (costItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<ValidationMessages> results = costItems.stream()
                .map(this::validateCostItem)
                .filter(this::nonEmpty)
                .collect(Collectors.toList());

        ValidationMessages emptyRowMessages = invokeEmptyRowValidator(costItems);
        if (emptyRowMessages != null) {
            results.add(emptyRowMessages);
        }

        return results;
    }

    private boolean nonEmpty(ValidationMessages validationMessages) {
        return validationMessages != null && validationMessages.hasErrors();
    }

    public ValidationMessages validateCostItem(FinanceRowItem costItem) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(costItem, "costItem");
        invokeValidator(costItem, bindingResult);
        return buildValidationMessages(costItem, bindingResult);
    }

    public ValidationMessages validateProjectCostItem(FinanceRowItem costItem) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(costItem, "costItem");
        invokeProjectCostValidator(costItem, bindingResult);
        return buildValidationMessages(costItem, bindingResult);
    }

    private ValidationMessages buildValidationMessages(FinanceRowItem costItem, BeanPropertyBindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("validated, with messages: ");
                bindingResult.getFieldErrors().stream().forEach(e -> LOG.debug("Field Error: " + e.getRejectedValue() + e.getDefaultMessage()));
                bindingResult.getAllErrors().stream().forEach(e -> LOG.debug("Error: " + e.getObjectName() + e.getDefaultMessage()));
            }
            return new ValidationMessages(costItem.getId(), bindingResult);
        } else {
            return ValidationMessages.noErrors(costItem.getId());
        }
    }

    private void invokeProjectCostValidator(FinanceRowItem costItem, BeanPropertyBindingResult bindingResult) {
        FinanceRowHandler financeRowHandler = applicationValidatorService.getProjectCostHandler(costItem);
        financeRowHandler.validate(costItem, bindingResult);
    }

    private void invokeValidator(FinanceRowItem costItem, BeanPropertyBindingResult bindingResult) {
        FinanceRowHandler financeRowHandler = applicationValidatorService.getCostHandler(costItem);
        financeRowHandler.validate(costItem, bindingResult);
    }

    private ValidationMessages invokeEmptyRowValidator(List<FinanceRowItem> costItems) {
        ValidationMessages validationMessages = new ValidationMessages();
        int rowCount = 0;
        if (costItems.size() > 1) {
            for (final FinanceRowItem row : costItems) {
                boolean exclude = row.excludeInRowCount();
                if (!exclude) {
                    rowCount++;
                }
            }
        }

        if (rowCount < costItems.get(0).getMinRows()) {
            switch (costItems.get(0).getCostType()) {
                case OTHER_FUNDING:
                    if ("Yes".equals(((OtherFunding) costItems.get(0)).getOtherPublicFunding())) {
                        if (costItems.get(0).getMinRows() == 1) {
                            validationMessages.addError(globalError("validation.finance.min.row.other.funding.single"));
                        } else {
                            validationMessages.addError(globalError("validation.finance.min.row.other.funding.multiple", singletonList(costItems.get(0).getMinRows())));
                        }
                    }
                    break;
                default:
                    validationMessages.addError(globalError("validation.finance.min.row", singletonList(costItems.get(0).getMinRows())));
                    break;
            }
        }
        return validationMessages;
    }
}
