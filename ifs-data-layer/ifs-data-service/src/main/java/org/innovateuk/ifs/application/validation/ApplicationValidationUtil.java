package org.innovateuk.ifs.application.validation;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.validator.*;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.validator.AcademicJesValidator;
import org.innovateuk.ifs.finance.validator.MinRowCountValidator;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.*;

import java.util.*;
import java.util.stream.Collectors;

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
    private MinRowCountValidator minRowCountValidator;

    @Autowired
    private AcademicJesValidator academicJesValidator;

    @ZeroDowntime(reference = "IFS-3366", description = "Remove old package names and add flyway script to correct them in database.")
    private static final Map<String, Class<?>> oldPackageClassMap = ImmutableMap.<String, Class<?>> builder()
            .put("org.innovateuk.ifs.validation.validator.EmailValidator", EmailValidator.class)
            .put("org.innovateuk.ifs.validation.validator.NotEmptyValidator", NotEmptyValidator.class)
            .put("org.innovateuk.ifs.validation.validator.WordCountValidator", WordCountValidator.class)
            .put("org.innovateuk.ifs.validation.validator.NonNegativeLongIntegerValidator", NonNegativeLongIntegerValidator.class)
            .put("org.innovateuk.ifs.validation.validator.SignedLongIntegerValidator", SignedLongIntegerValidator.class)
            .put("org.innovateuk.ifs.validation.validator.PastMMYYYYValidator", PastMMYYYYValidator.class)
            .build();

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
                                (v.getClazzName().equals(NotEmptyValidator.class.getName())
                                        || v.getClazzName().equals(NotEmptyValidator.OLD_PACKAGE_NAME)))) {

                            try {
                                validator = (Validator) context.getBean(Class.forName(v.getClazzName()));
                            } catch (ClassNotFoundException e) {
                                validator = (Validator) context.getBean(oldPackageClassMap.get(v.getClazzName()));
                            }

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

    public BindingResult validationApplicationDetails(Application application){
        DataBinder binder = new DataBinder(application);
        binder.addValidators(new ApplicationMarkAsCompleteValidator());
        binder.validate();
        return binder.getBindingResult();
    }

    public BindingResult validationJesForm(Application application) {
        DataBinder binder = new DataBinder(application);
        binder.addValidators(academicJesValidator);
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
        } else {
            for (FormInput formInput : formInputs) {
                validationMessages.addAll(isFormInputValid(application, formInput));
            }
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
            BindingResult validationResult = applicationValidatorService.validateFormInputResponse(application, formInput.getId(), markedAsCompleteById);

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
            FinanceRowType.fromType(formInput.getType()); // this checks if formInput is CostType related.
            validationMessages.addAll(applicationValidatorService.validateCostItem(application.getId(), question, markedAsCompleteById));
        } catch (IllegalArgumentException e) {
            // not a costtype, which is fine...
        }
    }

    private ValidationMessages invokeEmptyRowValidatorAndReturnMessages(List<FinanceRowItem> costItems, Question question) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(question, "question");
        invokeEmptyRowValidator(costItems, bindingResult);
        if (bindingResult.hasErrors()) {
            return new ValidationMessages(question.getId(), bindingResult);
        }
        return null;
    }

    public List<ValidationMessages> validateCostItem(List<FinanceRowItem> costItems, Question question) {
        if (costItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<ValidationMessages> results = costItems.stream()
                .map(this::validateCostItem)
                .filter(this::nonEmpty)
                .collect(Collectors.toList());

        ValidationMessages emptyRowMessages = invokeEmptyRowValidatorAndReturnMessages(costItems, question);
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

    private ValidationMessages buildValidationMessages(FinanceRowItem costItem, BeanPropertyBindingResult bindingResult){
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

    private void invokeEmptyRowValidator(List<FinanceRowItem> costItems, BeanPropertyBindingResult bindingResult) {
        ValidationUtils.invokeValidator(minRowCountValidator, costItems, bindingResult);
    }



}
