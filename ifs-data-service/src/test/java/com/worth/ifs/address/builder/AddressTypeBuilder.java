package com.worth.ifs.address.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.address.domain.AddressType;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class AddressTypeBuilder extends BaseBuilder<AddressType, AddressTypeBuilder> {

    private AddressTypeBuilder(List<BiConsumer<Integer, AddressType>> multiActions) {
        super(multiActions);
    }

    public static AddressTypeBuilder newAddressType() {
        return new AddressTypeBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AddressTypeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AddressType>> actions) {
        return new AddressTypeBuilder(actions);
    }

    @Override
    protected AddressType createInitial() {
        return new AddressType();
    }

    public AddressTypeBuilder withId(Long... ids) {
        return withArray((id, address) -> setField("id", id, address), ids);
    }

    public AddressTypeBuilder withName(String... names) {
        return withArray((name, addressType) -> setField("name", name, addressType), names);
    }
}