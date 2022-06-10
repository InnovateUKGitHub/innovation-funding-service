package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationPreRegConfigResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ApplicationPreRegConfigResourceBuilder extends BaseBuilder<ApplicationPreRegConfigResource, ApplicationPreRegConfigResourceBuilder> {

    private ApplicationPreRegConfigResourceBuilder(List<BiConsumer<Integer, ApplicationPreRegConfigResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected ApplicationPreRegConfigResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationPreRegConfigResource>> actions) {
        return new ApplicationPreRegConfigResourceBuilder(actions);
    }

    @Override
    protected ApplicationPreRegConfigResource createInitial() {
        return new ApplicationPreRegConfigResource();
    }

    public static ApplicationPreRegConfigResourceBuilder newApplicationPreRegConfigResource() {
        return new ApplicationPreRegConfigResourceBuilder(emptyList()).with(uniqueIds());
    }

    public ApplicationPreRegConfigResourceBuilder withApplicationId(Long... applicationId) {
        return withArraySetFieldByReflection("applicationId", applicationId);
    }

    public ApplicationPreRegConfigResourceBuilder withEnableForEOI(boolean... enableForEOI) {
        return withArraySetFieldByReflection("enableForEOI", enableForEOI);
    }
}
