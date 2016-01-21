package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.resource.OrganisationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Builder for OrganisationResource entities.
 */
public class OrganisationResourceBuilder extends BaseBuilder<OrganisationResource, OrganisationResourceBuilder> {

    private OrganisationResourceBuilder(List<BiConsumer<Integer, OrganisationResource>> multiActions) {
        super(multiActions);
    }

    public static OrganisationResourceBuilder newOrganisationResource() {
        return new OrganisationResourceBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedNames("OrganisationResource "));
    }

    @Override
    protected OrganisationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationResource>> actions) {
        return new OrganisationResourceBuilder(actions);
    }

    @Override
    protected OrganisationResource createInitial() {
        return new OrganisationResource();
    }

    public OrganisationResourceBuilder withId(Long... ids) {
        return withArray((id, organisation) -> setField("id", id, organisation), ids);
    }

    public OrganisationResourceBuilder withName(String... names) {
        return withArray((name, organisation) -> setField("name", name, organisation), names);
    }
    public OrganisationResourceBuilder withCompanyHouseNumber(String... numbers) {
        return withArray((number, organisation) -> setField("companyHouseNumber", number, organisation), numbers);
    }
}
