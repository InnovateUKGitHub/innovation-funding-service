package com.worth.ifs.bankdetails;

import com.worth.ifs.project.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import com.worth.ifs.project.bankdetails.service.BankDetailsRestService;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

/**
 * A service for dealing with project bank details via the appropriate Rest services
 */
@Service
public class BankDetailsServiceImpl implements BankDetailsService {

    private BankDetailsRestService bankDetailsRestService;

    @Autowired
    public BankDetailsServiceImpl(BankDetailsRestService bankDetailsRestService) {
        this.bankDetailsRestService = bankDetailsRestService;
    }

    @Override
    public BankDetailsResource getByProjectIdAndBankDetailsId(Long projectId, Long bankDetailsId) {
        return bankDetailsRestService.getByProjectIdAndBankDetailsId(projectId, bankDetailsId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> submitBankDetails(Long projectId, BankDetailsResource bankDetailsResource) {
        return bankDetailsRestService.submitBankDetails(projectId, bankDetailsResource).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateBankDetails(Long projectId, BankDetailsResource bankDetailsResource) {
        return bankDetailsRestService.updateBankDetails(projectId, bankDetailsResource).toServiceResult();
    }

    @Override
    public BankDetailsResource getBankDetailsByProjectAndOrganisation(Long projectId, Long organisationId) {
        return bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public ProjectBankDetailsStatusSummary getBankDetailsByProject(Long projectId) {
        return bankDetailsRestService.getBankDetailsStatusSummaryByProject(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ByteArrayResource downloadByCompetition(Long competitionId) {
        return bankDetailsRestService.downloadByCompetition(competitionId).getSuccessObjectOrThrowException();
    }
}
