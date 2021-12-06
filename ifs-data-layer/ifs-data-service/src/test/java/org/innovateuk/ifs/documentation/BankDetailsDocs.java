package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;

public class BankDetailsDocs {
    @SuppressWarnings("unchecked")
    public static final BankDetailsResourceBuilder bankDetailsResourceBuilder = newBankDetailsResource()
            .withId(1L)
            .withProject(1L)
            .withOrganisation(1L)
            .withSortCode("123456")
            .withAccountNumber("12345678")
            .withAddress(newAddressResource().build());

}
