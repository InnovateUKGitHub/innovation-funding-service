package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.stereotype.Service;

@Service
public class FileEntryRestServiceImpl extends BaseRestService implements FileEntryRestService {

    private String restUrl = "/fileentry";

    @Override
    public RestResult<FileEntryResource> findOne(Long id) {
        return getWithRestResult(restUrl + "/" + id, FileEntryResource.class);
    }
}
