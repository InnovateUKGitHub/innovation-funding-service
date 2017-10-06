package org.innovateuk.ifs.setup.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.setup.domain.SetupStatus;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface SetupStatusService {

    @SecuredBySpring(value = "FIND", description = "Only comp admins or projectfinances users can update the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<List<SetupStatus>> findByTargetIdAndTargetClassName(Long targetId, String targetClassName);

    @SecuredBySpring(value = "FIND", description = "Only comp admins or projectfinances users can update the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<List<SetupStatus>> findByTargetIdAndTargetClassNameAndParentId(Long targetId, String targetClassName, Long parentId);

    @SecuredBySpring(value = "FIND", description = "Only comp admins or projectfinances users can update the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<SetupStatusResource> findBySetupStatus(Long classPk, String className);

    @SecuredBySpring(value = "UPDATE", description = "Only comp admins or projectfinances users can update the status related to setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<SetupStatusResource> updateOrCreateSetupStatus(SetupStatusResource setupStatusResource);
}
