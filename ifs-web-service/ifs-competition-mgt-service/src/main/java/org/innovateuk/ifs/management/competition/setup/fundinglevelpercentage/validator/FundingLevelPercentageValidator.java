package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.validator;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelMaximumForm;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelPercentageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toMap;

@Component
public class FundingLevelPercentageValidator {

    @Autowired
    private CategoryRestService categoryRestService;

    public void validate(FundingLevelPercentageForm form, ValidationHandler validationHandler) {
        if (form.getMaximums().size() == 1) {
            validateSingleForm(form, validationHandler);
        } else {
            validateTableForm(form, validationHandler);
        }
    }

    private void validateTableForm(FundingLevelPercentageForm form, ValidationHandler validationHandler) {
        Map<Long, ResearchCategoryResource> researchCategories = categoryRestService.getResearchCategories().getSuccess()
                .stream()
                .collect(toMap(ResearchCategoryResource::getId, Function.identity()));
        IntStream.range(0, form.getMaximums().size()).forEach(index1 -> {
            IntStream.range(0, form.getMaximums().get(index1).size()).forEach(index2 -> {
                FundingLevelMaximumForm maximumForm = form.getMaximums().get(index1).get(index2);
                Integer maximum = maximumForm.getMaximum();
                if (maximum == null || maximum > 100 || maximum < 0) {
                    validationHandler.addError(Error.fieldError("maximums[" + index1 + "][" + index2 + "].maximum",
                            "maximum", "validation.competitionsetup.fundinglevelpercentage.table.numberbetween",
                            newArrayList(maximumForm.getOrganisationSize().getDescription(), researchCategories.get(maximumForm.getCategoryId()).getName(), 0, 100)));
                }
            });
        });
    }

    private void validateSingleForm(FundingLevelPercentageForm form, ValidationHandler validationHandler) {
        Integer maximum = form.getMaximums().get(0).get(0).getMaximum();
        if (maximum == null || maximum > 100 || maximum < 0) {
            validationHandler.addError(Error.fieldError("maximums[0][0].maximum", "maximum",
                    "validation.competitionsetup.fundinglevelpercentage.single.numberbetween", newArrayList(0, 100)));
        }
    }
}
