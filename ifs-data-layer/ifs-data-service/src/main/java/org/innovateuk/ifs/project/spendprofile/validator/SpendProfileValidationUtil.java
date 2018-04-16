package org.innovateuk.ifs.project.spendprofile.validator;

import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.validation.SpendProfileCostValidator;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import java.util.Optional;

@Component
public class SpendProfileValidationUtil {

    @Autowired
    private SpendProfileCostValidator spendProfileCostValidator;

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
}
