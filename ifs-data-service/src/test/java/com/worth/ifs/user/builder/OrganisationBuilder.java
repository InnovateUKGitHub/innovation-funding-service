package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationType;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Builder for Organisation entities.
 */
public class OrganisationBuilder extends BaseBuilder<Organisation, OrganisationBuilder> {

    private OrganisationBuilder(List<BiConsumer<Integer, Organisation>> multiActions) {
        super(multiActions);
    }

    public static OrganisationBuilder newOrganisation() {
        return new OrganisationBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedNames("Organisation "));
    }

    @Override
    protected OrganisationBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Organisation>> actions) {
        return new OrganisationBuilder(actions);
    }

    @Override
    protected Organisation createInitial() {
        return new Organisation();
    }

    public OrganisationBuilder withId(Long... ids) {
        return withArray((id, organisation) -> setField("id", id, organisation), ids);
    }

    public OrganisationBuilder withName(String... names) {
        return withArray((name, organisation) -> setField("name", name, organisation), names);
    }

    public OrganisationBuilder withCompanyHouseNumber(String... numbers) {
        return withArray((number, organisation) -> setField("companyHouseNumber", number, organisation), numbers);
    }

    public OrganisationBuilder withOrganisationType(OrganisationType... organisationTypes) {
        return withArray((organisationType, organisation) -> setField("organisationType", organisationType, organisation), organisationTypes);
    }
}
