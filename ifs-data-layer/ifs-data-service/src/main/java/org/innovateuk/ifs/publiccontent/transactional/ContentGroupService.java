package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Interface for public content actions.
 */
public interface ContentGroupService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "UPLOAD_CONTENT_GROUP_FILE",
            description = "The Competition Admin, or project finance user can upload a content group file.")
    ServiceResult<Void> uploadFile(long contentGroupId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "REMOVE_CONTENT_GROUP_FILE",
            description = "The Competition Admin, or project finance user can remove a content group file.")
    ServiceResult<Void> removeFile(Long contentGroupId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "SAVE_CONTENT_GROUPS",
            description = "The Competition Admin, or project finance user can remove a content group file.")
    ServiceResult<Void> saveContentGroups(PublicContentResource resource, PublicContent publicContent, PublicContentSectionType section);

    @PreAuthorize("hasPermission(#contentGroupId, 'org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupCompositeId', 'DOWNLOAD_CONTENT_GROUP_FILE')")
    ServiceResult<FileEntryResource> getFileDetails(@P("contentGroupId") long contentGroupId);


    @PreAuthorize("hasPermission(#contentGroupId, 'org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupCompositeId','DOWNLOAD_CONTENT_GROUP_FILE')")
    ServiceResult<FileAndContents> getFileContents(@P("contentGroupId")long contentGroupId);
}
