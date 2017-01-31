package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Service for operations around the usage and processing of public content.
 */
@Service
public class ContentGroupServiceImpl extends BaseTransactionalService implements ContentGroupService {


    @Override
    public ServiceResult<Void> uploadFile(long contentGroupId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return null;
    }

    @Override
    public ServiceResult<Void> removeFile(Long contentGroupId) {
        return null;
    }
}
