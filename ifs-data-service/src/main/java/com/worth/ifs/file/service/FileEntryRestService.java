package com.worth.ifs.file.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.file.resource.FileEntryResource;

public interface FileEntryRestService {

    RestResult<FileEntryResource> findOne(Long id);
}