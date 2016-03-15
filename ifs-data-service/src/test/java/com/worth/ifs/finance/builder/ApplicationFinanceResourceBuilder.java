package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
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

    public ApplicationFinanceResourceBuilder withApplication(Long applicationId) {
        return with(finance -> finance.setApplication(applicationId));
    }

    public ApplicationFinanceResourceBuilder withOrganisation(Long organisationId) {
        return with(finance -> finance.setOrganisation(organisationId));
    }
}
