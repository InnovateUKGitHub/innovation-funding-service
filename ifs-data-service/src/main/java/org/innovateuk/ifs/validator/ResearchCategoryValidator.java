package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.repository.ResearchCategoryRepository;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.rejectValue;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_RESEARCH_CATEGORY;

/**
 * Validator for Assessor Research Category questions.
 */
@Component
public class ResearchCategoryValidator extends BaseValidator {

    @Autowired
    private ResearchCategoryRepository researchCategoryRepository;

    @Override
    public void validate(Object target, Errors errors) {
        FormInputResponse response = (FormInputResponse) target;

        if (ASSESSOR_RESEARCH_CATEGORY == response.getFormInput().getType()) {
            List<ResearchCategory> researchCategories = researchCategoryRepository.findAll();
            Long value;

            try {
                value = Long.parseLong(response.getValue());
            } catch (NumberFormatException exception) {
                rejectValue(errors, "value", "validation.assessor.category.invalidCategory");
                return;
            }

            List<ResearchCategory> matchingCategories = researchCategories
                    .stream()
                    .filter(category -> value.equals(category.getId()))
                    .collect(toList());

            if (matchingCategories.isEmpty()) {
                rejectValue(errors, "value", "validation.assessor.category.invalidCategory");
            }
        }
    }
}
