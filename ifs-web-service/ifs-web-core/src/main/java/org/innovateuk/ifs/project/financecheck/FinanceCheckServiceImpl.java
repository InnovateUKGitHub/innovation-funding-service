package org.innovateuk.ifs.project.financecheck;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceQueriesRestService;
import org.innovateuk.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.upload.service.PostAttachmentRestService;
import org.innovateuk.thread.service.ThreadRestService;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FinanceCheckServiceImpl implements FinanceCheckService {

    @Autowired
    private FinanceCheckRestService financeCheckRestService;

    @Autowired
    @Qualifier("projectFinance")
    private PostAttachmentRestService attachmentService;

    @Autowired
    private ProjectFinanceQueriesRestService queryService;

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
        return attachmentService.find(fileId).toServiceResult();
    }

    @Override
    public ServiceResult<Long> saveQuery(QueryResource query) {
        return queryService.create(query).toServiceResult();
    }

    @Override
    public ServiceResult<List<QueryResource>> loadQueries(Long projectFinanceId) {
        return queryService.findAll(projectFinanceId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> savePost(PostResource post, long threadId) {
        return queryService.addPost(post, threadId).toServiceResult();
    }
}
