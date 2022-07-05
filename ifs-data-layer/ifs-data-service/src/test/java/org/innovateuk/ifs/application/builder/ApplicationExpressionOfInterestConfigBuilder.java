package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.ApplicationExpressionOfInterestConfig;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ApplicationExpressionOfInterestConfigBuilder extends BaseBuilder<ApplicationExpressionOfInterestConfig, ApplicationExpressionOfInterestConfigBuilder> {

    private ApplicationExpressionOfInterestConfigBuilder(List<BiConsumer<Integer, ApplicationExpressionOfInterestConfig>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected ApplicationExpressionOfInterestConfigBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationExpressionOfInterestConfig>> actions) {
        return new ApplicationExpressionOfInterestConfigBuilder(actions);
    }

    @Override
    protected ApplicationExpressionOfInterestConfig createInitial() {
        return new ApplicationExpressionOfInterestConfig();
    }
    public static ApplicationExpressionOfInterestConfigBuilder newApplicationExpressionOfInterestConfig() {
        return new ApplicationExpressionOfInterestConfigBuilder(emptyList()).with(uniqueIds());
    }

    public ApplicationExpressionOfInterestConfigBuilder withId(String... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public ApplicationExpressionOfInterestConfigBuilder withApplicationId(Long... applicationId) {
        return withArraySetFieldByReflection("applicationId", applicationId);
    }
    public ApplicationExpressionOfInterestConfigBuilder withEnabledForExpressionOfInterest(boolean... enabledForExpressionOfInterest) {
        return withArraySetFieldByReflection("enabledForExpressionOfInterest", enabledForExpressionOfInterest);
    }
}
