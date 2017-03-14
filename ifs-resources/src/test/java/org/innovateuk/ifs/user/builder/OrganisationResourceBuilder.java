package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

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

    public OrganisationResourceBuilder withOrganisationType(Long... organisationTypeIds) {
        return withArray((organisationTypeId, organisation) -> setField("organisationType", organisationTypeId, organisation), organisationTypeIds);
    }

    public OrganisationResourceBuilder withUsers(List<Long>... users) {
        return withArray((user, organisation) -> setField("users", user, organisation), users);
    }

    public OrganisationResourceBuilder withProcessRoles(List<Long>... processRoles) {
        return withArray((processRoleList, organisation) -> organisation.setProcessRoles(processRoleList), processRoles);
    }

    public OrganisationResourceBuilder withAddress(List<OrganisationAddressResource>... organisationAddressResource) {
    	return withArray((orgAddress, organisation) -> setField("addresses", orgAddress, organisation), organisationAddressResource);
    }

    public OrganisationResourceBuilder withOrganisationTypeName(String... organisationTypeNames) {
        return withArray((organisationTypeName, organisation) -> setField("organisationTypeName", organisationTypeName, organisation), organisationTypeNames);
    }
}
