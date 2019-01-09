package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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

    public ApplicationFinanceResourceBuilder withId(Long... ids) {
        return withArray((id, applicationFinanceResource) -> applicationFinanceResource.setId(id), ids);
    }

    public ApplicationFinanceResourceBuilder withMaximumFundingLevel(Integer... maximumFundingLevels) {
        return withArray((maximumFundingLevel, applicationFinanceResource) -> applicationFinanceResource.setMaximumFundingLevel(maximumFundingLevel), maximumFundingLevels);
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
