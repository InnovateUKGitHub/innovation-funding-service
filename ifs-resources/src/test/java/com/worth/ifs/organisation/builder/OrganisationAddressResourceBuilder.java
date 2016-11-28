package com.worth.ifs.organisation.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressTypeResource;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class OrganisationAddressResourceBuilder extends BaseBuilder<OrganisationAddressResource, OrganisationAddressResourceBuilder> {
    private OrganisationAddressResourceBuilder(List<BiConsumer<Integer, OrganisationAddressResource>> multiActions) {
        super(multiActions);
    }

    public static OrganisationAddressResourceBuilder newOrganisationAddressResource() {
        return new OrganisationAddressResourceBuilder(emptyList()).with(uniqueIds());
    }
    
    @Override
    protected OrganisationAddressResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationAddressResource>> actions) {
        return new OrganisationAddressResourceBuilder(actions);
    }
    
    @Override
    protected OrganisationAddressResource createInitial() {
        return new OrganisationAddressResource();
    }
    
    public OrganisationAddressResourceBuilder withAddressType(AddressTypeResource... addressTypesResource) {
        return withArray((addressType, orgAddress) -> setField("addressType", addressType, orgAddress), addressTypesResource);
    }
    
    public OrganisationAddressResourceBuilder withAddress(AddressResource... addresses) {
        return withArray((address, orgAddress) -> setField("address", address, orgAddress), addresses);
    }

    public OrganisationAddressResourceBuilder withOrganisation(Long... organisations){
        return withArray((organisation, orgId) -> setField("organisation", organisation, orgId), organisations);
    }

}
