package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryId;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.function.Supplier;


/**
 * Interface with security annotations for {@ApplicationFormInputUploadServiceImpl}.
 */
public interface ApplicationFormInputUploadService {
    @PreAuthorize("hasPermission(#fileEntry, 'UPDATE')")
    ServiceResult<FormInputResponseFileEntryResource> uploadResponse(FormInputResponseFileEntryResource fileEntry,
                                                                     Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#id, 'org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource', 'UPDATE')")
    ServiceResult<FormInputResponse> deleteFormInputResponseFileUpload(FormInputResponseFileEntryId id);

    @PreAuthorize("hasPermission(#id, 'org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource', 'READ')")
    ServiceResult<FormInputResponseFileAndContents> getFormInputResponseFileUpload(FormInputResponseFileEntryId id);
}
