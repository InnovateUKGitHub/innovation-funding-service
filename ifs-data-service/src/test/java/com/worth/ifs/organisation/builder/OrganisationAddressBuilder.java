package com.worth.ifs.organisation.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.user.domain.Organisation;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class OrganisationAddressBuilder extends BaseBuilder<OrganisationAddress, OrganisationAddressBuilder> {
    private OrganisationAddressBuilder(List<BiConsumer<Integer, OrganisationAddress>> multiActions) {
        super(multiActions);
    }

    public static OrganisationAddressBuilder newOrganisationAddress() {
        return new OrganisationAddressBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected OrganisationAddressBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationAddress>> actions) {
        return new OrganisationAddressBuilder(actions);
    }

    @Override
    protected OrganisationAddress createInitial() {
        return new OrganisationAddress();
    }

    public OrganisationAddressBuilder withAddressType(AddressType... addressTypes) {
        return withArray((addressType, orgAddress) -> setField("addressType", addressType, orgAddress), addressTypes);
    }

    public OrganisationAddressBuilder withAddress(Address... addresses) {
        return withArray((address, orgAddress) -> setField("address", address, orgAddress), addresses);
    }

    public OrganisationAddressBuilder withOrganisation(Organisation organisation){
        return with(orgAddress -> orgAddress.setOrganisation(organisation));
    }
}
