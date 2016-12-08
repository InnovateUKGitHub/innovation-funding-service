package com.worth.ifs.finance.builder;

import com.worth.ifs.finance.resource.ApplicationFinanceResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for ApplicationFinance entities.
 */
public class ApplicationFinanceResourceBuilder extends BaseFinanceResourceBuilder<ApplicationFinanceResource, ApplicationFinanceResourceBuilder> {

    public ApplicationFinanceResourceBuilder withFinanceFileEntry(Long financeFileEntry) {
        return with(finance -> finance.setFinanceFileEntry(financeFileEntry));
    }

    public ApplicationFinanceResourceBuilder withApplication(Long... applicationIds) {
        return withArray((applicationId, applicationFinanceResource) -> applicationFinanceResource.setApplication(applicationId), applicationIds);
    }

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
}
