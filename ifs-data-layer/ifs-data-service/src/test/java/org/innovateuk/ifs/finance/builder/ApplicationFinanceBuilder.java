package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.EmployeesAndTurnover;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.domain.Organisation;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.BuilderAmendFunctions.application;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for ApplicationFinance entities.
 */
public class ApplicationFinanceBuilder extends BaseBuilder<ApplicationFinance, ApplicationFinanceBuilder> {

    public ApplicationFinanceBuilder withApplication(Application application) {
        return with(application(application));
    }

    public ApplicationFinanceBuilder withOrganisationSize(OrganisationSize organisationSize) {
        return with(finance -> finance.setOrganisationSize(organisationSize));
    }

    public ApplicationFinanceBuilder withOrganisation(Organisation organisation) {
        return with(finance -> finance.setOrganisation(organisation));
    }

    public ApplicationFinanceBuilder withWorkPostcode(String workPostcode) {
        return with(finance -> finance.setWorkPostcode(workPostcode));
    }

    public ApplicationFinanceBuilder withEmployeesAndTurnover(EmployeesAndTurnover employeesAndTurnover) {
        return with(finance -> finance.setEmployeesAndTurnover(employeesAndTurnover));
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
