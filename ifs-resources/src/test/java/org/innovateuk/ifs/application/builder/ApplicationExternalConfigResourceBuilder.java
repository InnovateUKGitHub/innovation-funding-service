package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationExternalConfigResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ApplicationExternalConfigResourceBuilder extends BaseBuilder<ApplicationExternalConfigResource, ApplicationExternalConfigResourceBuilder> {

    private ApplicationExternalConfigResourceBuilder(List<BiConsumer<Integer, ApplicationExternalConfigResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected ApplicationExternalConfigResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationExternalConfigResource>> actions) {
        return new ApplicationExternalConfigResourceBuilder(actions);
    }

    @Override
    protected ApplicationExternalConfigResource createInitial() {
        return new ApplicationExternalConfigResource();
    }

    public static ApplicationExternalConfigResourceBuilder newApplicationExternalConfigResource() {
        return new ApplicationExternalConfigResourceBuilder(emptyList()).with(uniqueIds());
    }

    public ApplicationExternalConfigResourceBuilder withExternalApplicationId(String... externalApplicationId) {
        return withArraySetFieldByReflection("externalApplicationId", externalApplicationId);
    }

    public ApplicationExternalConfigResourceBuilder withExternalApplicantName(String... externalApplicantName) {
        return withArraySetFieldByReflection("externalApplicantName", externalApplicantName);
    }
}
