package com.worth.ifs.testdata;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.worth.ifs.testdata.IndustrialCostDataBuilder.newIndustrialCostData;
import static java.util.Collections.emptyList;


public class ApplicationFinanceDataBuilder extends BaseDataBuilder<ApplicationFinanceData, ApplicationFinanceDataBuilder> {

    public ApplicationFinanceDataBuilder withApplication(ApplicationResource application) {
        return with(data -> data.setApplication(application));
    }

    public ApplicationFinanceDataBuilder withOrganisation(String organisationName) {
        return with(data -> data.setOrganisation(retrieveOrganisationResourceByName(organisationName)));
    }

    public ApplicationFinanceDataBuilder withOrganisation(OrganisationResource organisation) {
        return with(data -> data.setOrganisation(organisation));
    }

    public ApplicationFinanceDataBuilder withUser(UserResource user) {
        return with(data -> data.setUser(user));
    }

    public ApplicationFinanceDataBuilder withIndustrialCosts(Function<IndustrialCostDataBuilder, IndustrialCostDataBuilder> costBuilderFn) {

        return doAsUser(data -> {

            ApplicationFinanceResource applicationFinance =
                    financeRowService.addCost(new ApplicationFinanceResourceId(data.getApplication().getId(), data.getOrganisation().getId())).
                            getSuccessObjectOrThrowException();

            IndustrialCostDataBuilder baseFinanceBuilder = newIndustrialCostData(serviceLocator).
                    withApplicationFinance(applicationFinance);

            costBuilderFn.apply(baseFinanceBuilder).build();
        });
    }

    private ApplicationFinanceDataBuilder doAsUser(Consumer<ApplicationFinanceData> action) {
        return with(data -> doAs(data.getUser(), () -> action.accept(data)));
    }

    public static ApplicationFinanceDataBuilder newApplicationFinanceData(ServiceLocator serviceLocator) {
        return new ApplicationFinanceDataBuilder(emptyList(), serviceLocator);
    }

    private ApplicationFinanceDataBuilder(List<BiConsumer<Integer, ApplicationFinanceData>> multiActions,
                                          ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected ApplicationFinanceDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationFinanceData>> actions) {
        return new ApplicationFinanceDataBuilder(actions, serviceLocator);
    }

    @Override
    protected ApplicationFinanceData createInitial() {
        return new ApplicationFinanceData();
    }
}
