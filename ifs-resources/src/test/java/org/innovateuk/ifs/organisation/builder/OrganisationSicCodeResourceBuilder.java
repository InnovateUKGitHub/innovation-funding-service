package org.innovateuk.ifs.organisation.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class OrganisationSicCodeResourceBuilder extends BaseBuilder<OrganisationSicCodeResource, OrganisationSicCodeResourceBuilder> {

    private OrganisationSicCodeResourceBuilder(List<BiConsumer<Integer, OrganisationSicCodeResource>> multiActions) {
        super(multiActions);
    }

    public static OrganisationSicCodeResourceBuilder newOrganisationSicCodeResource() {
        return new OrganisationSicCodeResourceBuilder(Collections.emptyList()).with(uniqueIds());
    }

    @Override
    protected OrganisationSicCodeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationSicCodeResource>> actions) {
        return new OrganisationSicCodeResourceBuilder(actions);
    }

    @Override
    protected OrganisationSicCodeResource createInitial() {
        return new OrganisationSicCodeResource();
    }

    public OrganisationSicCodeResourceBuilder withOrganisation(Long... organisations){
        return withArray((orgId, organisationSicCode) -> setField("organisation", orgId, organisationSicCode), organisations);
    }

    public OrganisationSicCodeResourceBuilder withSicCode(String... sicCodes) {
        return withArray((sicCode, organisationSicCode) -> setField("sicCode", sicCode, organisationSicCode), sicCodes);
    }
}
