package org.innovateuk.ifs.address.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AddressResourceBuilder extends BaseBuilder<AddressResource, AddressResourceBuilder> {
    private AddressResourceBuilder(List<BiConsumer<Integer, AddressResource>> multiActions) {
        super(multiActions);
    }

    public static AddressResourceBuilder newAddressResource() {
        return new AddressResourceBuilder(emptyList()).
                with(uniqueIds()).
                withAddressLine1("Line 1").
                withAddressLine2("Line 2").
                withAddressLine3("Line 3").
                withTown("My Town").
                withCounty("My County").
                withPostcode("MYP 05T");
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
}
