package com.worth.ifs.bankdetails.transactional;

import com.worth.ifs.address.repository.AddressRepository;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.bankdetails.domain.BankDetails;
import com.worth.ifs.bankdetails.mapper.BankDetailsMapper;
import com.worth.ifs.bankdetails.repository.BankDetailsRepository;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.organisation.repository.OrganisationAddressRepository;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class BankDetailsServiceImpl implements BankDetailsService{

    @Autowired
    BankDetailsMapper bankDetailsMapper;

    @Autowired
    BankDetailsRepository bankDetailsRepository;

    @Autowired
    OrganisationAddressRepository organisationAddressRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public ServiceResult<BankDetailsResource> getById(Long id) {
        return serviceSuccess(bankDetailsMapper.mapToResource(bankDetailsRepository.findOne(id)));
    }

    @Override
    public ServiceResult<Void> updateBankDetails(BankDetailsResource bankDetailsResource) {
        OrganisationAddressResource organisationAddressResource = bankDetailsResource.getOrganisationAddress();
        AddressResource addressResource = organisationAddressResource.getAddress();
        BankDetails bankDetails = bankDetailsMapper.mapToDomain(bankDetailsResource);

        if(organisationAddressResource.getId() != null){
            OrganisationAddress organisationAddress = organisationAddressRepository.findOne(organisationAddressResource.getId());
            bankDetails.setOrganisationAddress(organisationAddress);

            if(addressResource.getId() != null){
                organisationAddress.setAddress(addressRepository.findOne(addressResource.getId()));
            }
        }

        bankDetailsRepository.save(bankDetails);

        return serviceSuccess();
    }

    @Override
    public ServiceResult<BankDetailsResource> getByProjectAndOrganisation(Long projectId, Long organisationId) {
        BankDetails bankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId);
        if(bankDetails != null) {
            return serviceSuccess(bankDetailsMapper.mapToResource(bankDetails));
        } else {
            return serviceFailure(new Error("Bank details don't exist", HttpStatus.NOT_FOUND));
        }
    }
}
