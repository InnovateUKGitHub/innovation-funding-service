package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for OrganisationSizeResource entities.
 */
public class OrganisationSizeResourceBuilder extends BaseBuilder<OrganisationSizeResource, OrganisationSizeResourceBuilder> {

    public OrganisationSizeResourceBuilder withId(Long... projectId) {
        return withArray((id, sizeResource) -> sizeResource.setId(id), projectId);
    }

    public OrganisationSizeResourceBuilder WithDescription(String... projectFinanceId) {
        return withArray((id, sizeResource) -> sizeResource.setDescription(id), projectFinanceId);
    }

    private OrganisationSizeResourceBuilder(List<BiConsumer<Integer, OrganisationSizeResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static OrganisationSizeResourceBuilder newOrganisationSizeResource() {
        return new OrganisationSizeResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected OrganisationSizeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationSizeResource>> actions) {
        return new OrganisationSizeResourceBuilder(actions);
    }

    @Override
    protected OrganisationSizeResource createInitial() {
        return new OrganisationSizeResource();
    }
}
