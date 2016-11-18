package com.worth.ifs.address.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.address.resource.AddressTypeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class AddressTypeResourceBuilder extends BaseBuilder<AddressTypeResource, AddressTypeResourceBuilder> {
    private AddressTypeResourceBuilder(List<BiConsumer<Integer, AddressTypeResource>> multiActions) {
        super(multiActions);
    }

    public static AddressTypeResourceBuilder newAddressTypeResource() {
        return new AddressTypeResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AddressTypeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AddressTypeResource>> actions) {
        return new AddressTypeResourceBuilder(actions);
    }

    @Override
    protected AddressTypeResource createInitial() {
        return new AddressTypeResource();
    }

    public AddressTypeResourceBuilder withId(Long... ids) {
        return withArray((id, address) -> setField("id", id, address), ids);
    }

    public AddressTypeResourceBuilder withName(String... names) {
        return withArray((name, addressTypeResource) -> setField("name", name, addressTypeResource), names);
    }
}
