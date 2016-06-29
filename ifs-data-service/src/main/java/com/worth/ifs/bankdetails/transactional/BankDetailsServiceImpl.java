package com.worth.ifs.bankdetails.transactional;

import com.worth.ifs.bankdetails.domain.BankDetails;
import com.worth.ifs.bankdetails.mapper.BankDetailsMapper;
import com.worth.ifs.bankdetails.repository.BankDetailsRepository;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class BankDetailsServiceImpl implements BankDetailsService{

    @Autowired
    BankDetailsMapper bankDetailsMapper;

    @Autowired
    BankDetailsRepository bankDetailsRepository;

    @Override
    public ServiceResult<BankDetailsResource> getById(Long id) {
        return serviceSuccess(bankDetailsMapper.mapToResource(bankDetailsRepository.findOne(id)));
    }

    @Override
    public ServiceResult<Void> updateBankDetails(BankDetailsResource bankDetailsResource) {
        BankDetails bankDetails = bankDetailsMapper.mapToDomain(bankDetailsResource);
        //bankDetailsRepository.save(bankDetails);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<BankDetailsResource> getByProjectAndOrganisation(Long projectId, Long organisationId) {
        return serviceSuccess(bankDetailsMapper.mapToResource(bankDetailsRepository.findByProjectIdAndOrganisationAddressOrganisationId(projectId, organisationId)));
    }
}
