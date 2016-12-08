package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationSize;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.application;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for ApplicationFinance entities.
 */
public class ApplicationFinanceBuilder extends BaseBuilder<ApplicationFinance, ApplicationFinanceBuilder> {

    public ApplicationFinanceBuilder withApplication(Application application) {
        return with(application(application));
    }

    public ApplicationFinanceBuilder withOrganisationSize(Organisation organisation) {
        return with(finance -> setField("organisation", organisation, finance));
    }

    public ApplicationFinanceBuilder withOrganisationSize(OrganisationSize organisationSize) {
        return with(finance -> finance.setOrganisationSize(organisationSize));
    }

    private ApplicationFinanceBuilder(List<BiConsumer<Integer, ApplicationFinance>> newMultiActions) {
        super(newMultiActions);
    }

    public static ApplicationFinanceBuilder newApplicationFinance() {
        return new ApplicationFinanceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ApplicationFinanceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationFinance>> actions) {
        return new ApplicationFinanceBuilder(actions);
    }

    @Override
    protected ApplicationFinance createInitial() {
        return new ApplicationFinance();
    }
}
