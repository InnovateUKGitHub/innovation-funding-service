package org.innovateuk.ifs.organisation.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationExecutiveOfficerResource;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class OrganisationExecutiveOfficerResourceBuilder extends BaseBuilder<OrganisationExecutiveOfficerResource, OrganisationExecutiveOfficerResourceBuilder> {

    private OrganisationExecutiveOfficerResourceBuilder(List<BiConsumer<Integer, OrganisationExecutiveOfficerResource>> multiActions) {
        super(multiActions);
    }

    public static OrganisationExecutiveOfficerResourceBuilder newOrganisationExecutiveOfficerResource() {
        return new OrganisationExecutiveOfficerResourceBuilder(Collections.emptyList()).with(uniqueIds());
    }

    @Override
    protected OrganisationExecutiveOfficerResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationExecutiveOfficerResource>> actions) {
        return new OrganisationExecutiveOfficerResourceBuilder(actions);
    }

    @Override
    protected OrganisationExecutiveOfficerResource createInitial() {
        return new OrganisationExecutiveOfficerResource();
    }

    public OrganisationExecutiveOfficerResourceBuilder withOrganisation(Long... organisations){
        return withArray((orgId, organisationExecutiveOfficer) -> setField("organisation", orgId, organisationExecutiveOfficer), organisations);
    }

    public OrganisationExecutiveOfficerResourceBuilder withName(String... names) {
        return withArray((sicCode, organisationExecutiveOfficer) -> setField("name", sicCode, organisationExecutiveOfficer), names);
    }
}
