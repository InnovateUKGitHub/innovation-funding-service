package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.user.domain.Organisation;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Builder for ApplicationFinance entities.
 */
public class ApplicationFinanceResourceBuilder extends BaseBuilder<ApplicationFinanceResource, ApplicationFinanceResourceBuilder> {

    private ApplicationFinanceResourceBuilder(List<BiConsumer<Integer, ApplicationFinanceResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static ApplicationFinanceResourceBuilder newApplicationFinanceResource() {
        return new ApplicationFinanceResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ApplicationFinanceResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationFinanceResource>> actions) {
        return new ApplicationFinanceResourceBuilder(actions);
    }

    @Override
    protected ApplicationFinanceResource createInitial() {
        return new ApplicationFinanceResource();
    }

    public ApplicationFinanceResourceBuilder withApplication(Application application) {
        return with(application(application));
    }

    public ApplicationFinanceResourceBuilder withOrganisation(Organisation organisation) {
        return with(finance -> setField("organisation", organisation, finance));
    }
}
