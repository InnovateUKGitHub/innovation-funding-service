package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;

public interface FileEntryRestService {

    RestResult<FileEntryResource> findOne(Long id);
}
