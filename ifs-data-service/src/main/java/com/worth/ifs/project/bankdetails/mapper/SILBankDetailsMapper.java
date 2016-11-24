package com.worth.ifs.project.bankdetails.mapper;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.project.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.sil.experian.resource.AccountDetails;
import com.worth.ifs.sil.experian.resource.Address;
import com.worth.ifs.sil.experian.resource.SILBankDetails;

/**
 * Mapping results retrieved from the postcode web API to address resources.
 */
public class SILBankDetailsMapper {
    public AccountDetails toAccountDetails(BankDetailsResource bankDetailsResource) {
        Address address = null;

        if(bankDetailsResource.getOrganisationAddress() != null && bankDetailsResource.getOrganisationAddress().getAddress() != null){
            AddressResource addressResource = bankDetailsResource.getOrganisationAddress().getAddress();
            address = new Address(null,
                    addressResource.getAddressLine1(),
                    addressResource.getAddressLine2(),
                    addressResource.getAddressLine3() ,
                    addressResource.getTown(),
                    addressResource.getPostcode());
        }

        return new AccountDetails(
                bankDetailsResource.getSortCode(),
                bankDetailsResource.getAccountNumber(),
                bankDetailsResource.getCompanyName(),
                bankDetailsResource.getRegistrationNumber(),
                address
        );
    }

    public SILBankDetails toSILBankDetails(BankDetailsResource bankDetailsResource){
        SILBankDetails silBankDetails = new SILBankDetails();
        silBankDetails.setAccountNumber(bankDetailsResource.getAccountNumber());
        silBankDetails.setSortcode(bankDetailsResource.getSortCode());
        return silBankDetails;
    }
}