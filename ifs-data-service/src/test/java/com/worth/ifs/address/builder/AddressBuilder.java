package com.worth.ifs.address.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.address.domain.Address;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class AddressBuilder extends BaseBuilder<Address, AddressBuilder> {

    public static AddressBuilder newAddress() {
        return new AddressBuilder(emptyList()).with(uniqueIds());
    }

    private AddressBuilder(List<BiConsumer<Integer, Address>> multiActions) {
        super(multiActions);
    }

    @Override
    protected AddressBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Address>> actions) {
        return new AddressBuilder(actions);
    }

    @Override
    protected Address createInitial() {
        return new Address();
    }
}
