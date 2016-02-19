package com.worth.ifs.file.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.file.resource.FileEntryResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileEntryRestServiceImpl extends BaseRestService implements FileEntryRestService {

    @Value("${ifs.data.service.rest.fileentry}")
    private String restUrl;

    @Override
    public RestResult<FileEntryResource> findOne(Long id) {
        return getWithRestResult(restUrl + "/" + id, FileEntryResource.class);
    }
}