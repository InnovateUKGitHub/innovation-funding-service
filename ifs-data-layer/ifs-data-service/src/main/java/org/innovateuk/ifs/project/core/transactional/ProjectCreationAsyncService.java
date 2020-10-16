package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ProjectCreationAsyncService {

    Future<ServiceResult<Void>> createAsync(Function<Long, ServiceResult<Void>> postCreationFunction,
                                             Long applicationId,
                                             Consumer<Long> rollbackFunction);

}
