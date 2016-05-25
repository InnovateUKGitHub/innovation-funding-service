package com.worth.ifs.application.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileEntryRestService;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import com.worth.ifs.finance.service.CostRestService;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * {@code FinanceServiceImpl} implements {@link FinanceService} handles the finances for each of the organisations.
 */
// TODO DW - INFUND-1555 - get the service calls below to use RestResults
@Service
public class FinanceServiceImpl implements FinanceService {

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private CostRestService costRestService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Override
    public ApplicationFinanceResource addApplicationFinance(Long userId, Long applicationId) {
        ProcessRoleResource processRole = userRestService.findProcessRole(userId, applicationId).getSuccessObjectOrThrowException();

        if(processRole.getOrganisation()!=null) {
            return applicationFinanceRestService.addApplicationFinanceForOrganisation(applicationId, processRole.getOrganisation()).getSuccessObjectOrThrowException();
        }
        return null;
    }

    @Override
    public ApplicationFinanceResource getApplicationFinance(Long userId, Long applicationId) {
        ProcessRoleResource userApplicationRole = userRestService.findProcessRole(userId, applicationId).getSuccessObjectOrThrowException();
        return applicationFinanceRestService.getApplicationFinance(applicationId, userApplicationRole.getOrganisation()).getSuccessObjectOrThrowException();
    }

    @Override
    public ApplicationFinanceResource getApplicationFinanceDetails(Long userId, Long applicationId) {
        ProcessRoleResource userApplicationRole = userRestService.findProcessRole(userId, applicationId).getSuccessObjectOrThrowException();
        return applicationFinanceRestService.getFinanceDetails(applicationId, userApplicationRole.getOrganisation()).getSuccessObjectOrThrowException();
    }


    @Override
    public List<ApplicationFinanceResource> getApplicationFinanceTotals(Long applicationId) {
        return applicationFinanceRestService.getFinanceTotals(applicationId).handleSuccessOrFailure(
                failure -> Collections.<ApplicationFinanceResource> emptyList(),
                success -> success
        );
    }


    @Override
    public List<CostItem> getCosts(Long applicationFinanceId) {
       return costRestService.getCosts(applicationFinanceId).getSuccessObjectOrThrowException();
    }

    @Override
    public CostItem addCost(Long applicationFinanceId, Long questionId) {
        return costRestService.add(applicationFinanceId, questionId, null).getSuccessObjectOrThrowException();
    }

    @Override
    public RestResult<FileEntryResource> addFinanceDocument(Long applicationFinanceId, String contentType, long contentLength, String originalFilename, byte[] file) {
        return applicationFinanceRestService.addFinanceDocument(applicationFinanceId, contentType, contentLength, originalFilename, file);
    }

    @Override
    public RestResult<Void> removeFinanceDocument(Long applicationFinanceId) {
        return applicationFinanceRestService.removeFinanceDocument(applicationFinanceId);
    }

    @Override
    public RestResult<FileEntryResource> getFinanceEntry(Long applicationFinanceId) {
        return fileEntryRestService.findOne(applicationFinanceId);
    }

    @Override
    public RestResult<ByteArrayResource> getFinanceDocumentByApplicationFinance(Long applicationFinanceId) {
        return applicationFinanceRestService.getFile(applicationFinanceId);
    }
}
