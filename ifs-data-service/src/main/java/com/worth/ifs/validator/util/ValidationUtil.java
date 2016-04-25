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
import com.worth.ifs.validator.AcademicValidator;
import com.worth.ifs.validator.GrantClaimValidator;
import com.worth.ifs.validator.NotEmptyValidator;
import com.worth.ifs.validator.OtherFundingValidator;
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
    public static GrantClaimValidator grantClaimValidator;
    public static OtherFundingValidator otherFundingValidator;
    public static AcademicValidator academicValidator;

    @Autowired
    private ValidationUtil(ValidatorService validatorService, @Qualifier("basicValidator") Validator validator, GrantClaimValidator grantClaimValidator, OtherFundingValidator otherFundingValidator, AcademicValidator academicValidator) {
        this.validatorService = validatorService;
        this.validator = validator;
        this.grantClaimValidator = grantClaimValidator;
        this.otherFundingValidator = otherFundingValidator;
        this.academicValidator = academicValidator;
    }

    public static BindingResult validateResponse(FormInputResponse response, boolean ignoreEmpty) {
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
        LOG.debug("VALIDATE SECTION "+ section.getName());
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
        LOG.debug("==validate question "+ question.getName());
        boolean questionValid = true;
        List<ValidationMessages> validationMessages = new ArrayList<>();
        if (question.hasMultipleStatuses()) {
            for (FormInput formInput : question.getFormInputs()) {
                validationMessages.addAll(isFormInputValid(question, application, markedAsCompleteById, questionValid, formInput));
            }
//            validationMessages.addAll(validatorService.validateCostItem(application.getId(), question.getId(), markedAsCompleteById));
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
        LOG.debug("====validate form input "+ formInput.getDescription());
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

    private static void validationCostItem(Question question, Application application, Long markedAsCompleteById, FormInput formInput, List<ValidationMessages> validationMessages) {
        try {
            CostType costType = CostType.fromString(formInput.getFormInputType().getTitle()); // this checks if formInput is CostType related.
            validationMessages.addAll(validatorService.validateCostItem(application.getId(), question.getId(), markedAsCompleteById));
        } catch (IllegalArgumentException e) {
            // not a costtype, which is fine...
        }
    }

    public static List<ValidationMessages> validateCostItem(List<CostItem> costItems) {
        if (costItems.isEmpty())
            return Collections.emptyList();

        List<ValidationMessages> results = costItems.stream()
                .map(ValidationUtil::validateCostItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return results;
    }

    public static ValidationMessages validateCostItem(CostItem costItem) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(costItem, "costItem");
        ValidationUtils.invokeValidator(validator, costItem, bindingResult);

        invokeExtraValidator(costItem, bindingResult);

        if (bindingResult.hasErrors()) {
            if(LOG.isDebugEnabled()){
                LOG.debug("validated, with messages: ");
                bindingResult.getFieldErrors().stream().forEach(e -> LOG.debug("Field Error: "+ e.getRejectedValue() + e.getDefaultMessage()));
                bindingResult.getAllErrors().stream().forEach(e -> LOG.debug("Error: "+ e.getObjectName() + e.getDefaultMessage()));
            }
            return new ValidationMessages(costItem.getId(), bindingResult);
        } else {
            LOG.debug("validated, no messages");
            return null;
        }

    }

    private static void invokeExtraValidator(CostItem costItem, BeanPropertyBindingResult bindingResult) {
        Validator extraValidator = null;
        switch(costItem.getCostType()){
            case FINANCE:
                extraValidator = grantClaimValidator;
                break;
            case OTHER_FUNDING:
                extraValidator = otherFundingValidator;
                break;
            case ACADEMIC:
                extraValidator = academicValidator;
                break;
        }
        if(extraValidator != null){
            LOG.info("invoke extra validator: "+ extraValidator.getClass().toString());
            ValidationUtils.invokeValidator(extraValidator, costItem, bindingResult);
        }
    }
}
