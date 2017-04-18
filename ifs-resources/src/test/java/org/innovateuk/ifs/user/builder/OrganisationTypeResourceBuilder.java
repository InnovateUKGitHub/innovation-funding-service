package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;
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

    public OrganisationTypeResourceBuilder withId(Long... ids) {
        return withArray((id, organisationTypeResource) -> organisationTypeResource.setId(id), ids);
    }

    public OrganisationTypeResourceBuilder withName(String... names) {
        return withArray((name, organisationTypeResource) -> organisationTypeResource.setName(name), names);
    }

    public OrganisationTypeResourceBuilder withVisibleInSetup(Boolean... visibleInSetups) {
        return withArray((visibleInSetup, organisationTypeResource) -> organisationTypeResource.setVisibleInSetup(visibleInSetup), visibleInSetups);
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
