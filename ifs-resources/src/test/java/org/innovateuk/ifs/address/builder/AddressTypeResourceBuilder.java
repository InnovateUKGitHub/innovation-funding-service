package org.innovateuk.ifs.address.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.resource.AddressTypeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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
