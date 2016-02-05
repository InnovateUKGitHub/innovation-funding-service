package com.worth.ifs.file.service;

import com.worth.ifs.file.resource.FileEntryResource;

public interface FileEntryRestService {
    FileEntryResource findOne(Long id);
}