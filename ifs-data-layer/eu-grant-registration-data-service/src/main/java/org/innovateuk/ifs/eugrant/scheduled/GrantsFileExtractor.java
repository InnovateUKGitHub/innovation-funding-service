package org.innovateuk.ifs.eugrant.scheduled;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * TODO DW - document this class
 */
@Component
public class GrantsFileExtractor {

    ServiceResult<List<ServiceResult<EuGrantResource>>> processFile(File file) {
        return serviceSuccess(emptyList());
    }
}
