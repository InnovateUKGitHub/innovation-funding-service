package org.innovateuk.ifs.workflow;

import org.innovateuk.ifs.workflow.resource.ProcessOutcomeResource;
import org.innovateuk.ifs.workflow.service.ProcessOutcomeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains methods to retrieve and store {@link ProcessOutcomeResource} related data,
 * through the RestService {@link org.innovateuk.ifs.workflow.service.ProcessOutcomeRestService}.
 */
@Service
public class ProcessOutcomeServiceImpl implements ProcessOutcomeService {

    @Autowired
    private ProcessOutcomeRestService processOutcomeRestService;

    @Override
    public ProcessOutcomeResource getById(Long id) {
        return processOutcomeRestService.findOne(id).getSuccessObjectOrThrowException();
    }

    @Override
    public ProcessOutcomeResource getByProcessId(Long processId) {
        return processOutcomeRestService.findLatestByProcessId(processId).getSuccessObjectOrThrowException();
    }

    @Override
    public ProcessOutcomeResource getByProcessIdAndOutcomeType(Long processId, String outcomeType) {
        return processOutcomeRestService.findLatestByProcessIdAndType(processId, outcomeType).getSuccessObjectOrThrowException();
    }

}
