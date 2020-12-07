package org.innovateuk.ifs.organisation.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.organisation.resource.HeukarPartnerOrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class HeukarPartnerOrganisationResourceBuilder extends BaseBuilder<HeukarPartnerOrganisationResource, HeukarPartnerOrganisationResourceBuilder> {

    public HeukarPartnerOrganisationResourceBuilder(List<BiConsumer<Integer, HeukarPartnerOrganisationResource>> actions) {
        super(actions);
    }

    public static HeukarPartnerOrganisationResourceBuilder newHeukarPartnerOrganisationResource() {
        return new HeukarPartnerOrganisationResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected HeukarPartnerOrganisationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, HeukarPartnerOrganisationResource>> actions) {
        return new HeukarPartnerOrganisationResourceBuilder(actions);
    }

    @Override
    protected HeukarPartnerOrganisationResource createInitial() {
        return new HeukarPartnerOrganisationResource();
    }

    public HeukarPartnerOrganisationResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public HeukarPartnerOrganisationResourceBuilder withApplicationId(Long... ids) {
        return withArraySetFieldByReflection("applicationId", ids);
    }

    public HeukarPartnerOrganisationResourceBuilder withOrganisationTypeResource(OrganisationTypeResource... organisationTypeResources) {
        return withArray((organisationTypeResource, heukarPartnerOrganisationResource) ->
                setField("organisationTypeResource", organisationTypeResource, heukarPartnerOrganisationResource), organisationTypeResources);
    }

}
