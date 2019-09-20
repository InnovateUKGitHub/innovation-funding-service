package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;

public class OrganisationFinancesWithoutGrowthTableResourceBuilder
        extends BaseBuilder<OrganisationFinancesWithoutGrowthTableResource, OrganisationFinancesWithoutGrowthTableResourceBuilder> {

    protected OrganisationFinancesWithoutGrowthTableResourceBuilder() {
        super();
    }

    protected OrganisationFinancesWithoutGrowthTableResourceBuilder(List<BiConsumer<Integer, OrganisationFinancesWithoutGrowthTableResource>> newActions) {
        super(newActions);
    }

    public static OrganisationFinancesWithoutGrowthTableResourceBuilder newOrganisationFinancesWithoutGrowthTableResource() {
        return new OrganisationFinancesWithoutGrowthTableResourceBuilder();
    }

    @Override
    protected OrganisationFinancesWithoutGrowthTableResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationFinancesWithoutGrowthTableResource>> actions) {
        return new OrganisationFinancesWithoutGrowthTableResourceBuilder(actions);
    }

    @Override
    protected OrganisationFinancesWithoutGrowthTableResource createInitial() {
        return createDefault(OrganisationFinancesWithoutGrowthTableResource.class);
    }
}