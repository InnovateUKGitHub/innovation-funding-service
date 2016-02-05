package com.worth.ifs.file.service;

import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.security.NotSecured;

public interface FileEntryRestService {
    @NotSecured("REST Service")
    FileEntryResource findOne(Long id);
}