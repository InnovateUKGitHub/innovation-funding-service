package org.innovateuk.ifs.application.finance.service;

import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRowRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * {@code FinanceServiceImpl} implements {@link FinanceService} handles the finances for each of the organisations.
 */
@Service
public class FinanceServiceImpl implements FinanceService {

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private ApplicationFinanceRowRestService financeRowRestService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationService applicationService;

    @Override
    public ApplicationFinanceResource getApplicationFinance(Long userId, Long applicationId) {
        ProcessRoleResource userApplicationRole = processRoleRestService.findProcessRole(userId, applicationId).getSuccess();
        return applicationFinanceRestService.getApplicationFinance(applicationId, userApplicationRole.getOrganisationId()).getSuccess();
    }

    @Override
    public ApplicationFinanceResource getApplicationFinanceByApplicationIdAndOrganisationId(Long applicationId, Long organisationId) {
        return applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();
    }

    @Override
    public ApplicationFinanceResource getApplicationFinanceDetails(Long userId, Long applicationId) {
        ProcessRoleResource userApplicationRole = processRoleRestService.findProcessRole(userId, applicationId).getSuccess();
        return getApplicationFinanceDetails(userId, applicationId, userApplicationRole.getOrganisationId());
    }

    @Override
    public ApplicationFinanceResource getApplicationFinanceDetails(Long userId, Long applicationId, Long organisationId) {
        return applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
    }

    @Override
    public List<ApplicationFinanceResource> getApplicationFinanceDetails(Long applicationId) {
        return applicationFinanceRestService.getFinanceDetails(applicationId).handleSuccessOrFailure(
                failure -> emptyList(),
                success -> success
        );
    }

    @Override
    public List<ApplicationFinanceResource> getApplicationFinanceTotals(Long applicationId) {
        return applicationFinanceRestService.getFinanceTotals(applicationId).handleSuccessOrFailure(
                failure -> emptyList(),
                success -> success
        );
    }

    @Override
    public RestResult<FileEntryResource> getFinanceEntry(Long applicationFinanceFileEntryId) {
        return fileEntryRestService.findOne(applicationFinanceFileEntryId);
    }

    @Override
    public RestResult<FileEntryResource> getFinanceEntryByApplicationFinanceId(Long applicationFinanceId) {
        return applicationFinanceRestService.getFileDetails(applicationFinanceId);
    }

    @Override
    public RestResult<ByteArrayResource> getFinanceDocumentByApplicationFinance(Long applicationFinanceId) {
        return applicationFinanceRestService.getFile(applicationFinanceId);
    }
}
