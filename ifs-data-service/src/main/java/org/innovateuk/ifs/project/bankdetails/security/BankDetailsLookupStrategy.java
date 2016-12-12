package org.innovateuk.ifs.project.bankdetails.security;

import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.mapper.BankDetailsMapper;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class BankDetailsLookupStrategy {

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @Autowired
    private BankDetailsMapper bankDetailsMapper;

    @PermissionEntityLookupStrategy
    public BankDetails getBankDetails(Long bankDetailsId) {
        return bankDetailsRepository.findOne(bankDetailsId);
    }

    @PermissionEntityLookupStrategy
    public BankDetailsResource getBankDetailsResource(Long bankDetailsId) {
        return bankDetailsMapper.mapToResource(bankDetailsRepository.findOne(bankDetailsId));
    }
}
