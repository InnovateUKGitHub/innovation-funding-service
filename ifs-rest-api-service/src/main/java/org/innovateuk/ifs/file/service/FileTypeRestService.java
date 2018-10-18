package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileTypeResource;

/**
 * Interface for CRUD operations on {@link FileTypeResource} related data.
 */
public interface FileTypeRestService {
    RestResult<FileTypeResource> findOne(long id);
    RestResult<FileTypeResource> findByName(String name);
}

