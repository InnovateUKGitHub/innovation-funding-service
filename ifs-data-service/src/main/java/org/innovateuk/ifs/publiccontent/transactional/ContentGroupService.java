package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
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

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "GET_CONTENT_GROUP_FILE_DETAILS",
            description = "The Competition Admin, or project finance user can read a content group file details.")
    ServiceResult<FileEntryResource> getFileDetails(long contentGroupId);


    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "GET_CONTENT_GROUP_FILE_CONTENTS",
            description = "The Competition Admin, or project finance user can read a content group file contents.")
    ServiceResult<FileAndContents> getFileContents(long contentGroupId);
}
