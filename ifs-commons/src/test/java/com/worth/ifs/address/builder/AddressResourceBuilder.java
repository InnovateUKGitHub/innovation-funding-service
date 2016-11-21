package com.worth.ifs.address.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.address.resource.AddressResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class AddressResourceBuilder extends BaseBuilder<AddressResource, AddressResourceBuilder> {
    private AddressResourceBuilder(List<BiConsumer<Integer, AddressResource>> multiActions) {
        super(multiActions);
    }

    public static AddressResourceBuilder newAddressResource() {
        return new AddressResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AddressResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AddressResource>> actions) {
        return new AddressResourceBuilder(actions);
    }

    @Override
    protected AddressResource createInitial() {
        return new AddressResource();
    }

    public AddressResourceBuilder withId(Long... ids) {
        return withArray((id, address) -> setField("id", id, address), ids);
    }

    public AddressResourceBuilder withAddressLine1(String... addressLines) {
        return withArray((addressLine, address) -> setField("addressLine1", addressLine, address), addressLines);
    }

    public AddressResourceBuilder withAddressLine2(String... addressLines) {
        return withArray((addressLine, address) -> setField("addressLine2", addressLine, address), addressLines);
    }

    public AddressResourceBuilder withAddressLine3(String... addressLines) {
        return withArray((addressLine, address) -> setField("addressLine3", addressLine, address), addressLines);
    }

    public AddressResourceBuilder withTown(String... towns) {
        return withArray((town, address) -> setField("town", town, address), towns);
    }

    public AddressResourceBuilder withCounty(String... counties) {
        return withArray((county, address) -> setField("county", county, address), counties);
    }

    public AddressResourceBuilder withPostcode(String... postcodes) {
        return withArray((postcode, address) -> setField("postcode", postcode, address), postcodes);
    }

    @SafeVarargs
    public final AddressResourceBuilder withOrganisationList(List<Long>... organisationLists) {
        return withArray((organisationList, address) -> setField("organisations", organisationList, address), organisationLists);
    }

}
