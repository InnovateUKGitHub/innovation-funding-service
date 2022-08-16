package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationExpressionOfInterestConfigResource;
import org.innovateuk.ifs.application.resource.ApplicationExternalConfigResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ApplicationExpressionOfInterestConfigResourceBuilder extends BaseBuilder<ApplicationExpressionOfInterestConfigResource, ApplicationExpressionOfInterestConfigResourceBuilder> {

    private ApplicationExpressionOfInterestConfigResourceBuilder(List<BiConsumer<Integer, ApplicationExpressionOfInterestConfigResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected ApplicationExpressionOfInterestConfigResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationExpressionOfInterestConfigResource>> actions) {
        return new ApplicationExpressionOfInterestConfigResourceBuilder(actions);
    }

    @Override
    protected ApplicationExpressionOfInterestConfigResource createInitial() {
        return new ApplicationExpressionOfInterestConfigResource();
    }
    public static ApplicationExpressionOfInterestConfigResourceBuilder newApplicationExpressionOfInterestConfigResource() {
        return new ApplicationExpressionOfInterestConfigResourceBuilder(emptyList()).with(uniqueIds());
    }

    public ApplicationExpressionOfInterestConfigResourceBuilder withId(String... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public ApplicationExpressionOfInterestConfigResourceBuilder withApplicationId(Long... applicationId) {
        return withArraySetFieldByReflection("applicationId", applicationId);
    }

    public ApplicationExpressionOfInterestConfigResourceBuilder withEnabledForExpressionOfInterest(boolean... enabledForExpressionOfInterest) {
        return withArraySetFieldByReflection("enabledForExpressionOfInterest", enabledForExpressionOfInterest);
    }

    public ApplicationExpressionOfInterestConfigResourceBuilder withEoiApplicationId(Long... eoiApplicationId) {
        return withArraySetFieldByReflection("eoiApplicationId", eoiApplicationId);
    }
}
