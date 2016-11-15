package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.resource.OrganisationTypeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Builder for OrganisationResource entities.
 */
public class OrganisationTypeResourceBuilder extends BaseBuilder<OrganisationTypeResource, OrganisationTypeResourceBuilder> {

    private OrganisationTypeResourceBuilder(List<BiConsumer<Integer, OrganisationTypeResource>> multiActions) {
        super(multiActions);
    }

    public static OrganisationTypeResourceBuilder newOrganisationTypeResource() {
        return new OrganisationTypeResourceBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedNames("OrganisationTypeResource "));
    }

    @Override
    protected OrganisationTypeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationTypeResource>> actions) {
        return new OrganisationTypeResourceBuilder(actions);
    }

    @Override
    protected OrganisationTypeResource createInitial() {
        return new OrganisationTypeResource();
    }
}
