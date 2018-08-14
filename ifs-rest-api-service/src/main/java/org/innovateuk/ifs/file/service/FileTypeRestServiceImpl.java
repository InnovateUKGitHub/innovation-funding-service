package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileTypeResource;
import org.springframework.stereotype.Service;

/**
 * Implements {@link FileTypeRestService}
 */
@Service
public class FileTypeRestServiceImpl extends BaseRestService implements FileTypeRestService {

    private String fileTypeRestURL = "/file/file-type";

    @Override
    public RestResult<FileTypeResource> findOne(long id) {
        return getWithRestResult(fileTypeRestURL + "/" + id, FileTypeResource.class);
    }

    @Override
    public RestResult<FileTypeResource> findByName(String name) {
        return getWithRestResult(fileTypeRestURL + "/find-by-name/" + name, FileTypeResource.class);
    }
}


