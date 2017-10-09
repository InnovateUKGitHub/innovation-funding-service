package org.innovateuk.ifs.setup.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.setupStatusResourceListType;

@Service
public class SetupStatusRestServiceImpl extends BaseRestService implements SetupStatusRestService {
    private static final String SETUP_STATUS_URL = "setupStatus/";

    @Override
    public RestResult<List<SetupStatusResource>> findByTarget(String targetClassName, Long targetId) {
        return getWithRestResult(SETUP_STATUS_URL + "findByTarget/" + targetClassName + "/" + targetId, setupStatusResourceListType());
    }

    @Override
    public RestResult<List<SetupStatusResource>> findByTargetAndParent(String targetClassName, Long targetId, Long parentId) {
        return getWithRestResult(SETUP_STATUS_URL + "findByTargetAndParent/" + targetClassName + "/" + targetId + "/" + parentId, setupStatusResourceListType());
    }

    @Override
    public RestResult<List<SetupStatusResource>> findByClassAndParent(String className, Long parentId) {
        return getWithRestResult(SETUP_STATUS_URL + "findByClassAndParent/" + className + "/" + parentId, setupStatusResourceListType());
    }

    @Override
    public RestResult<List<SetupStatusResource>> findSetupStatus(String className, Long classPk) {
        return getWithRestResult(SETUP_STATUS_URL + "findSetupStatus/" + className + "/" + classPk, setupStatusResourceListType());
    }

    @Override
    public RestResult<SetupStatusResource> saveSetupStatus(SetupStatusResource setupStatusResource) {
        return postWithRestResult(SETUP_STATUS_URL + "save", setupStatusResource, SetupStatusResource.class);
    }
}
