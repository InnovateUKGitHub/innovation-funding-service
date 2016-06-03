package com.worth.ifs.file.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.file.resource.FileEntryResource;
import org.springframework.stereotype.Service;

@Service
public class FileEntryRestServiceImpl extends BaseRestService implements FileEntryRestService {

    private String restUrl = "/fileentry";

    @Override
    public RestResult<FileEntryResource> findOne(Long id) {
        return getWithRestResult(restUrl + "/" + id, FileEntryResource.class);
    }
}