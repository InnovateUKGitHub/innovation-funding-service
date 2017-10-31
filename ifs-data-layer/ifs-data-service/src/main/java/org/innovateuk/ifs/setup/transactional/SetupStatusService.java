package org.innovateuk.ifs.setup.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for handling setup status objects
 */
public interface SetupStatusService {

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can read the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<List<SetupStatusResource>> findByTargetClassNameAndTargetId(String targetClassName, Long targetId);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can read the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<List<SetupStatusResource>> findByTargetClassNameAndTargetIdAndParentId(String targetClassName, Long targetId, Long parentId);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can read the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<List<SetupStatusResource>> findByClassNameAndParentId(String className, Long parentId);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can read the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<SetupStatusResource> findSetupStatus(String className, Long classPk);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can read the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<SetupStatusResource> findSetupStatusAndTarget(String className, Long classPk, String targetClassName, Long targetId);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can update the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<SetupStatusResource> saveSetupStatus(SetupStatusResource setupStatusResource);
}
