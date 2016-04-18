package com.worth.ifs.validator.util;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.domain.FormValidator;
import com.worth.ifs.validator.NotEmptyValidator;
import com.worth.ifs.validator.transactional.ValidatorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.*;

import java.util.*;
import java.util.stream.Collectors;

@Component
public final class ValidationUtil {
    public final static Log LOG = LogFactory.getLog(ValidationUtil.class);
    public static ValidatorService validatorService;
    public static Validator validator;

    @Autowired
    private ValidationUtil(ValidatorService validatorService, @Qualifier("basicValidator") Validator validator) {
        this.validatorService = validatorService;
        this.validator = validator;
    }

    public static BindingResult validateResponse(FormInputResponse response, boolean ignoreEmpty) {
        if (response == null) {
            LOG.info("response is null");
        }
        if (response.getFormInput() == null) {
            LOG.info("response has no formInputs");
        }
        if (response.getFormInput().getFormValidators() == null) {
            LOG.info("response has no formValidators");
        }
        Set<FormValidator> validators = response.getFormInput().getFormValidators();

        DataBinder binder = new DataBinder(response);

        // Get validators from the FormInput, and add to binder.
        validators.forEach(
                v ->
                {
                    Validator validator = null;
                    try {
                        if (!(ignoreEmpty && v.getClazzName().equals(NotEmptyValidator.class.getName()))) {
                            validator = (Validator) Class.forName(v.getClazzName()).getConstructor().newInstance();
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

    public static List<ValidationMessages> isSectionValid(Long markedAsCompleteById, Section section, Application application) {
        List<ValidationMessages> validationMessages = new ArrayList<>();
        boolean allQuestionsValid = true;
        for (Question question : section.fetchAllChildQuestions()) {
            if (!question.getMarkAsCompletedEnabled()) {
                // no need to validate :)
            } else {
                validationMessages.addAll(isQuestionValid(question, application, markedAsCompleteById));
            }
        }
        return validationMessages;
    }

    public static List<ValidationMessages> isQuestionValid(Question question, Application application, Long markedAsCompleteById) {
        boolean questionValid = true;
        List<ValidationMessages> validationMessages = new ArrayList<>();
        if (question.hasMultipleStatuses()) {
            for (FormInput formInput : question.getFormInputs()) {
                validationMessages.addAll(isFormInputValid(question, application, markedAsCompleteById, questionValid, formInput));
            }
        } else {
            for (FormInput formInput : question.getFormInputs()) {
                validationMessages.addAll(isFormInputValid(application, formInput));
            }
        }
        return validationMessages;
    }

    public static List<ValidationMessages> isFormInputValid(Application application, FormInput formInput) {
        List<ValidationMessages> validationMessages = new ArrayList<>();
        List<BindingResult> bindingResults = validatorService.validateFormInputResponse(application.getId(), formInput.getId());
        for (BindingResult bindingResult : bindingResults) {
            if (bindingResult.hasErrors()) {
                validationMessages.add(new ValidationMessages(formInput.getId(), bindingResult));
            }
        }
        return null;
    }

    public static List<ValidationMessages> isFormInputValid(Question question, Application application, Long markedAsCompleteById, boolean questionValid, FormInput formInput) {
        List<ValidationMessages> validationMessages = new ArrayList<>();
        if (formInput.getFormValidators().isEmpty()) {
            // no validator? question is valid!
        } else {
            BindingResult validationResult = validatorService.validateFormInputResponse(application.getId(), formInput.getId(), markedAsCompleteById);

            if (validationResult.hasErrors()) {
                validationMessages.add(new ValidationMessages(formInput.getId(), validationResult));
            }
        }

        CostType costType = null;
        try {
            costType = CostType.fromString(formInput.getFormInputType().getTitle());
        } catch (IllegalArgumentException e) {
            // not a costtype, which is fine...
        }
        if (costType != null) {
            validationMessages.addAll(validatorService.validateCostItem(application.getId(), costType.name(), question.getId(), markedAsCompleteById));
        }
        return validationMessages;
    }

    public static List<ValidationMessages> validateCostItem(List<CostItem> costItems) {
        if (costItems.isEmpty())
            return Collections.emptyList();

        LOG.debug("validateCostItem list : " + costItems.size());
        List<ValidationMessages> results = costItems.stream()
                .map(ValidationUtil::validateCostItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return results;
    }

    public static ValidationMessages validateCostItem(CostItem costItem) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(costItem, "costItem");
        ValidationUtils.invokeValidator(validator, costItem, bindingResult);
        if (bindingResult.hasErrors()) {
            LOG.debug("validated, with messages: ");
            bindingResult.getFieldErrors().stream().forEach(e -> LOG.debug("Field Error: " + e.getDefaultMessage()));
            return new ValidationMessages(costItem.getId(), bindingResult);
        } else {
            LOG.debug("validated, no messages");
            return null;
        }

    }
}
