package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileTypeResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface FileTypeService {

    @SecuredBySpring(value = "READ", description = "Currently only comp admin, project finance or IFS admin can retrieve a file type by id. This API can be opened up to others as and when required")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<FileTypeResource> findOne(long id);

    @SecuredBySpring(value = "READ", description = "Currently only comp admin, project finance or IFS admin can retrieve a file type by name. This API can be opened up to others as and when required")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<FileTypeResource> findByName(String name);
}
