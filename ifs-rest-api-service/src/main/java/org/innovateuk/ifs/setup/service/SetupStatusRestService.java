package org.innovateuk.ifs.setup.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;

import java.util.List;

/**
 * Rest service for getting and saving the setup statuses
 */
public interface SetupStatusRestService {
    RestResult<List<SetupStatusResource>> findByTarget(String targetClassName, Long targetId);

    RestResult<List<SetupStatusResource>> findByTargetAndParent(String targetClassName, Long targetId, Long parentId);

    RestResult<List<SetupStatusResource>> findByClassAndParent(String className, Long parentId);

    RestResult<List<SetupStatusResource>> findSetupStatus(String className, Long classPk);

    RestResult<SetupStatusResource> saveSetupStatus(SetupStatusResource setupStatusResource);
}
