package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.KtpYearResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesKtpYearsResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import java.time.YearMonth;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;

public class OrganisationFinancesKtpYearsResourceBuilder extends BaseBuilder<OrganisationFinancesKtpYearsResource, OrganisationFinancesKtpYearsResourceBuilder> {

    protected OrganisationFinancesKtpYearsResourceBuilder() {
        super();
    }

    protected OrganisationFinancesKtpYearsResourceBuilder(List<BiConsumer<Integer, OrganisationFinancesKtpYearsResource>> newActions) {
        super(newActions);
    }

    public static OrganisationFinancesKtpYearsResourceBuilder newOrganisationFinancesKtpYearsResource() {
        return new OrganisationFinancesKtpYearsResourceBuilder();
    }
    
    @Override
    protected OrganisationFinancesKtpYearsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
            OrganisationFinancesKtpYearsResource>> actions) {
        return new OrganisationFinancesKtpYearsResourceBuilder(actions);
    }

    @Override
    protected OrganisationFinancesKtpYearsResource createInitial() {
        return createDefault(OrganisationFinancesKtpYearsResource.class);
    }

    public OrganisationFinancesKtpYearsResourceBuilder withOrganisationSize(OrganisationSize... organisationSizes) {
        return withArray((size, resource) -> resource.setOrganisationSize(size), organisationSizes);
    }

    public OrganisationFinancesKtpYearsResourceBuilder withGroupEmployees(Long... array) {
        return withArray((item, resource) -> resource.setGroupEmployees(item), array);
    }

    public OrganisationFinancesKtpYearsResourceBuilder withFinancialYearEnd(YearMonth... array) {
        return withArray((item, resource) -> resource.setFinancialYearEnd(item), array);
    }

    @SafeVarargs
    public final OrganisationFinancesKtpYearsResourceBuilder withKtpYears(List<KtpYearResource>... array) {
        return withArray((item, resource) -> resource.setYears(item), array);
    }
}
