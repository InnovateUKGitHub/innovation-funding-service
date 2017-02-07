package org.innovateuk.ifs.project.financecheck;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FinanceCheckServiceImpl implements FinanceCheckService {

    @Autowired
    private FinanceCheckRestService financeCheckRestService;

    @Autowired
    private PostAttachmentRestService attachmentService;

    @Override
    public FinanceCheckResource getByProjectAndOrganisation(ProjectOrganisationCompositeId key){
        return financeCheckRestService.getByProjectAndOrganisation(key.getProjectId(), key.getOrganisationId()).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> update(FinanceCheckResource toUpdate){
        return financeCheckRestService.update(toUpdate).toServiceResult();
    }

    @Override
    public ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId) {
        return financeCheckRestService.getFinanceCheckSummary(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> approveFinanceCheck(Long projectId, Long organisationId) {
        return financeCheckRestService.approveFinanceCheck(projectId, organisationId).toServiceResult();
    }

    @Override
    public FinanceCheckProcessResource getFinanceCheckApprovalStatus(Long projectId, Long organisationId) {
        return financeCheckRestService.getFinanceCheckApprovalStatus(projectId, organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public FinanceCheckEligibilityResource getFinanceCheckEligibilityDetails(Long projectId, Long organisationId) {
        return financeCheckRestService.getFinanceCheckEligibilityDetails(projectId, organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<FileEntryResource> uploadFile(String contentType, long contentLength, String originalFilename, byte[] bytes) {
        return attachmentService.upload(contentType, contentLength, originalFilename, bytes).toServiceResult();
    }

    @Override
    public ServiceResult<Void> deleteFile(Long fileId) {
        return attachmentService.delete(fileId).toServiceResult();
    }

    @Override
    public ServiceResult<Optional<ByteArrayResource>> downloadFile(Long fileId) {
        return attachmentService.download(fileId).toServiceResult();
    }

    @Override
    public ServiceResult<FileEntryResource> getFileInfo(Long fileId) {
        return attachmentService.getFileInfo(fileId).toServiceResult();
    }
}
