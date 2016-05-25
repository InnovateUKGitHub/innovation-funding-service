package com.worth.ifs.organisation.builder;

import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;
import static com.worth.ifs.BuilderAmendFunctions.setField;

import java.util.List;
import java.util.function.BiConsumer;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.address.resource.AddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;

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
    
    public OrganisationAddressResourceBuilder withAddressType(AddressType... addressTypes) {
        return withArray((addressType, orgAddress) -> setField("addressType", addressType, orgAddress), addressTypes);
    }
    
    public OrganisationAddressResourceBuilder withAddress(AddressResource... addresses) {
        return withArray((address, orgAddress) -> setField("address", address, orgAddress), addresses);
    }

}
