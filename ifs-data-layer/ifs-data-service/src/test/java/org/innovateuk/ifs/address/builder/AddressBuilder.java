package org.innovateuk.ifs.address.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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

    public AddressBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public AddressBuilder withAddressLine1(String... addressLine1s) {
        return withArray((addressLine1, address) -> setField("addressLine1", addressLine1, address), addressLine1s);
    }

    public AddressBuilder withAddressLine2(String... addressLine2s) {
        return withArray((addressLine2, address) -> setField("addressLine2", addressLine2, address), addressLine2s);
    }

    public AddressBuilder withAddressLine3(String... addressLine3s) {
        return withArray((addressLine3, address) -> setField("addressLine3", addressLine3, address), addressLine3s);
    }

    public AddressBuilder withTown(String... towns) {
        return withArray((town, address) -> setField("town", town, address), towns);
    }

    public AddressBuilder withCounty(String... counties) {
        return withArray((county, address) -> setField("county", county, address), counties);
    }

    public AddressBuilder withCountry(String... countries) {
        return withArray((country, address) -> setField("country", country, address), countries);
    }

    public AddressBuilder withPostcode(String... postcodes) {
        return withArray((postcode, address) -> setField("postcode", postcode, address), postcodes);
    }

    @Override
    protected Address createInitial() {
        return new Address();
    }
}
