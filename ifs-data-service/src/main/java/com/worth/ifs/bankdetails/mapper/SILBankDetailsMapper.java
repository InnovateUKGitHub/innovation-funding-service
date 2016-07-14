package com.worth.ifs.bankdetails.mapper;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.resource.experian.AccountDetails;
import com.worth.ifs.bankdetails.resource.experian.Address;

/**
 * Mapping results retrieved from the postcode web API to address resources.
 */
public class SILBankDetailsMapper {
    public AccountDetails toResource(BankDetailsResource bankDetailsResource) {
        Address address = null;

        if(bankDetailsResource.getOrganisationAddress() != null && bankDetailsResource.getOrganisationAddress().getAddress() != null){
            AddressResource addressResource = bankDetailsResource.getOrganisationAddress().getAddress();
            address = new Address(addressResource.getAddressLine1(),
                    addressResource.getAddressLine2(),
                    addressResource.getAddressLine3(),
                    null ,
                    addressResource.getTown(),
                    addressResource.getPostcode());
        }

        return new AccountDetails(
                bankDetailsResource.getSortCode(),
                bankDetailsResource.getAccountNumber(),
                bankDetailsResource.getOrganisation().getName(),
                bankDetailsResource.getOrganisation().getCompanyHouseNumber(),
                address
        );
    }
}