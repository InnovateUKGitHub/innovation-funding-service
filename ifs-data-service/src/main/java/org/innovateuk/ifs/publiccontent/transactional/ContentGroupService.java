package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Interface for public content actions.
 */
public interface ContentGroupService {


    ServiceResult<Void> uploadFile(long contentGroupId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    ServiceResult<Void> removeFile(Long contentGroupId);

//    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
//    @SecuredBySpring(value = "GET_PUBLIC_CONTENT",
//            description = "The Competition Admin, or project finance user can get the public content for a competition.")
//    ServiceResult<PublicContentResource> findByCompetitionId(Long id);

}
