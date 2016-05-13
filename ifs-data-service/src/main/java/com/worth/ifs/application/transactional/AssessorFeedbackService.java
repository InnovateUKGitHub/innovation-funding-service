package com.worth.ifs.application.transactional;

import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.security.NotSecured;

import java.io.InputStream;
import java.util.function.Supplier;

public interface AssessorFeedbackService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<AssessorFeedbackResource> findOne(Long id);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<AssessorFeedbackResource> findByAssessorId(Long assessorId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> createAssessorFeedbackFileEntry(long applicationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> updateAssessorFeedbackFileEntry(long applicationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> deleteAssessorFeedbackFileEntry(long applicationId);
}