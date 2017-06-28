package org.innovateuk.ifs.project.bankdetails.mapper;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.sil.experian.resource.AccountDetails;
import org.innovateuk.ifs.sil.experian.resource.Address;
import org.innovateuk.ifs.sil.experian.resource.SILBankDetails;

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
