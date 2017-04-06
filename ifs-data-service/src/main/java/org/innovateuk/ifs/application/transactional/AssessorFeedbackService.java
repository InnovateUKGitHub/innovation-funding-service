package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.function.Supplier;

public interface AssessorFeedbackService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPLOAD_ASSESSOR_FEEDBACK')")
    ServiceResult<FileEntryResource> createAssessorFeedbackFileEntry(@P("applicationId") long applicationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'DOWNLOAD_ASSESSOR_FEEDBACK')")
    ServiceResult<FileAndContents> getAssessorFeedbackFileEntryContents(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'DOWNLOAD_ASSESSOR_FEEDBACK')")
    ServiceResult<FileEntryResource> getAssessorFeedbackFileEntryDetails(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPLOAD_ASSESSOR_FEEDBACK')")
    ServiceResult<Void> updateAssessorFeedbackFileEntry(long applicationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'REMOVE_ASSESSOR_FEEDBACK')")
    ServiceResult<Void> deleteAssessorFeedbackFileEntry(long applicationId);
}
