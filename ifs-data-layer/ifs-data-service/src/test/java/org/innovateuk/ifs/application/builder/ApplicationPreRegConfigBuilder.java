package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationPreRegConfig;
import org.innovateuk.ifs.application.resource.ApplicationPreRegConfigResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ApplicationPreRegConfigBuilder extends BaseBuilder<ApplicationPreRegConfig, ApplicationPreRegConfigBuilder> {

    private ApplicationPreRegConfigBuilder(List<BiConsumer<Integer, ApplicationPreRegConfig>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected ApplicationPreRegConfigBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationPreRegConfig>> actions) {
        return new ApplicationPreRegConfigBuilder(actions);
    }

    @Override
    protected ApplicationPreRegConfig createInitial() {
        return new ApplicationPreRegConfig();
    }

    public static ApplicationPreRegConfigBuilder newApplicationPreRegConfig() {
        return new ApplicationPreRegConfigBuilder(emptyList()).with(uniqueIds());
    }

    public ApplicationPreRegConfigBuilder withApplication(Application... applications) {
        return withArraySetFieldByReflection("application", applications);
    }

    public ApplicationPreRegConfigBuilder withEnableForEOI(boolean... enableForEOI) {
        return withArraySetFieldByReflection("enableForEOI", enableForEOI);
    }
}

