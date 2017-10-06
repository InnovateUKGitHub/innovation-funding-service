package org.innovateuk.ifs.setup.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface SetupStatusService {

    @SecuredBySpring(value = "FIND", description = "Only comp admins or projectfinances users can update the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Iterable<SetupStatusResource>> findByTargetIdAndTargetClassName(Long targetId, String targetClassName);

    @SecuredBySpring(value = "FIND", description = "Only comp admins or projectfinances users can update the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Iterable<SetupStatusResource>> findByTargetClassNameAndTargetIdAndParentId(String targetClassName, Long targetId, Long parentId);

    @SecuredBySpring(value = "FIND", description = "Only comp admins or projectfinances users can update the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Iterable<SetupStatusResource>> findByTargetClassNameAndParentId(String targetClassName, Long parentId);

    @SecuredBySpring(value = "FIND", description = "Only comp admins or projectfinances users can update the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<SetupStatusResource> findSetupStatus(Long classPk, String className);

    @SecuredBySpring(value = "UPDATE", description = "Only comp admins or projectfinances users can update the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<SetupStatusResource> saveSetupStatus(SetupStatusResource setupStatusResource);
}
