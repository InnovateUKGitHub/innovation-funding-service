package org.innovateuk.ifs.validator.util;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.validation.SpendProfileCostValidator;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.innovateuk.ifs.validator.ApplicationMarkAsCompleteValidator;
import org.innovateuk.ifs.validator.MinRowCountValidator;
import org.innovateuk.ifs.validator.NotEmptyValidator;
import org.innovateuk.ifs.validator.transactional.ValidatorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.groups.Default;
import java.util.*;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.rest.ValidationMessages.reject;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.rejectValue;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;

@Component
public class ValidationUtil {
    private final static Log LOG = LogFactory.getLog(ValidationUtil.class);
    private ValidatorService validatorService;
    private MinRowCountValidator minRowCountValidator;
    private SpendProfileCostValidator spendProfileCostValidator;

    @Autowired
    ApplicationContext context;


    @Autowired
    @Lazy
    private ValidationUtil(ValidatorService validatorService,
                           MinRowCountValidator minRowCountValidator,
                           SpendProfileCostValidator spendProfileCostValidator
    ) {
        this.validatorService = validatorService;
        this.minRowCountValidator = minRowCountValidator;
        this.spendProfileCostValidator = spendProfileCostValidator;
    }

    /**
     * This method is needed because we want to add validator Group to validation.
     * Because we can't use the spring validators for this, we need to convert the validation messages.
     * {@link http://docs.oracle.com/javaee/6/tutorial/doc/gkagv.html}
     */
    public static boolean isValid(Errors result, Object o, Class<?>... classes) {
        if (classes == null || classes.length == 0 || classes[0] == null) {
            classes = new Class<?>[]{Default.class};
        }
        javax.validation.Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(o, classes);
        addValidationMessages(result, violations);
        return violations.size() == 0;
    }

    public static void addValidationMessages(Errors result, Set<ConstraintViolation<Object>> violations) {
        for (ConstraintViolation<Object> v : violations) {
            Path path = v.getPropertyPath();
            String propertyName = "";
            if (path != null) {
                for (Path.Node n : path) {
                    propertyName += n.getName() + ".";
                }
                propertyName = propertyName.substring(0, propertyName.length() - 1);
            }

            Map<String, Object> attributes = v.getConstraintDescriptor().getAttributes();
            Map<String, Object> messageArguments =
                    attributes != null ?
                            simpleFilter(attributes, (key, value) -> !asList("groups", "message", "payload", "inclusive").contains(key))
                            : emptyMap();

            List<Object> messageArgumentValues = new ArrayList<>(messageArguments.values());

            if (propertyName == null || "".equals(propertyName)) {
                reject(result, v.getMessage(), messageArgumentValues.toArray());
            } else {
                rejectValue(result, propertyName, v.getMessage(), messageArgumentValues.toArray());
            }
        }
    }

    public BindingResult validateResponse(FormInputResponse response, boolean ignoreEmpty) {
        DataBinder binder = new DataBinder(response);
        if (response == null) {
            LOG.info("response is null");
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
                        if (!(ignoreEmpty && v.getClazzName().equals(NotEmptyValidator.class.getName()))) {
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

    public BindingResult validationApplicationDetails(Application application){
        DataBinder binder = new DataBinder(application);
        binder.addValidators(new ApplicationMarkAsCompleteValidator());
        binder.validate();
        return binder.getBindingResult();
    }

    public List<ValidationMessages> isSectionValid(Long markedAsCompleteById, Section section, Application application) {
        LOG.debug("VALIDATE SECTION " + section.getName());
        List<ValidationMessages> validationMessages = new ArrayList<>();
        for (Question question : section.fetchAllChildQuestions()) {
            if (question.getMarkAsCompletedEnabled()) {
                validationMessages.addAll(isQuestionValid(question, application, markedAsCompleteById));
            }
        }
        return validationMessages;
    }

    public List<ValidationMessages> isQuestionValid(Question question, Application application, Long markedAsCompleteById) {
        LOG.debug("==validate question " + question.getName());
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
        List<BindingResult> bindingResults = validatorService.validateFormInputResponse(application.getId(), formInput.getId());
        for (BindingResult bindingResult : bindingResults) {
            if (bindingResult.hasErrors()) {
                validationMessages.add(new ValidationMessages(formInput.getId(), bindingResult));
            }
        }
        return validationMessages;
    }

    private List<ValidationMessages> isFormInputValid(Question question, Application application, Long markedAsCompleteById, FormInput formInput) {
        LOG.debug("====validate form input " + formInput.getDescription());
        List<ValidationMessages> validationMessages = new ArrayList<>();
        if (formInput.getFormValidators().isEmpty()) {
            // no validator? question is valid!
        } else {
            BindingResult validationResult = validatorService.validateFormInputResponse(application.getId(), formInput.getId(), markedAsCompleteById);

            if (validationResult.hasErrors()) {
                validationMessages.add(new ValidationMessages(formInput.getId(), validationResult));
            }
        }

        validationCostItem(question, application, markedAsCompleteById, formInput, validationMessages);
        return validationMessages;
    }

    private void validationCostItem(Question question, Application application, Long markedAsCompleteById, FormInput formInput, List<ValidationMessages> validationMessages) {
        try {
            FinanceRowType.fromType(formInput.getType()); // this checks if formInput is CostType related.
            validationMessages.addAll(validatorService.validateCostItem(application.getId(), question, markedAsCompleteById));
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

    public Optional<ValidationMessages> validateSpendProfileTableResource(SpendProfileTableResource tableResource) {

        Optional<ValidationMessages> result = Optional.empty();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(tableResource, "spendProfileTable");
        ValidationUtils.invokeValidator(spendProfileCostValidator, tableResource, bindingResult);

        if (bindingResult.hasErrors()) {
            ValidationMessages messages = new ValidationMessages(bindingResult);
            result = Optional.of(messages);
        }

        return result;
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
            LOG.debug("validated, no messages");
            return ValidationMessages.noErrors(costItem.getId());
        }
    }

    private void invokeProjectCostValidator(FinanceRowItem costItem, BeanPropertyBindingResult bindingResult) {
        FinanceRowHandler financeRowHandler = validatorService.getProjectCostHandler(costItem);
        financeRowHandler.validate(costItem, bindingResult);
    }

    private void invokeValidator(FinanceRowItem costItem, BeanPropertyBindingResult bindingResult) {
        FinanceRowHandler financeRowHandler = validatorService.getCostHandler(costItem);
        financeRowHandler.validate(costItem, bindingResult);
    }

    private void invokeEmptyRowValidator(List<FinanceRowItem> costItems, BeanPropertyBindingResult bindingResult) {
        ValidationUtils.invokeValidator(minRowCountValidator, costItems, bindingResult);
    }
}
