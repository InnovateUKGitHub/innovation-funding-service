package org.innovateuk.ifs.workflow.transactional;

import org.innovateuk.ifs.assessment.repository.ProcessOutcomeRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;
import org.innovateuk.ifs.workflow.mapper.ProcessOutcomeMapper;
import org.innovateuk.ifs.workflow.resource.ProcessOutcomeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProcessOutcomeServiceImpl extends BaseTransactionalService implements ProcessOutcomeService {

    @Autowired
    private ProcessOutcomeRepository repository;

    @Autowired
    private ProcessOutcomeMapper processOutcomeMapper;

    @Override
    public ServiceResult<ProcessOutcomeResource> findOne(Long id) {
        return find(repository.findOne(id), notFoundError(ProcessOutcome.class, id)).andOnSuccessReturn(processOutcomeMapper::mapToResource);
    }

    @Override
    public ServiceResult<ProcessOutcomeResource> findLatestByProcess(Long processId) {
        return find(repository.findTopByProcessIdOrderByIdDesc(processId), notFoundError(ProcessOutcome.class, processId)).andOnSuccessReturn(processOutcomeMapper::mapToResource);
    }

    @Override
    public ServiceResult<ProcessOutcomeResource> findLatestByProcessAndOutcomeType(Long processId, String outcomeType) {
        return find(repository.findTopByProcessIdAndOutcomeTypeOrderByIdDesc(processId, outcomeType), notFoundError(ProcessOutcome.class, processId, outcomeType)).andOnSuccessReturn(processOutcomeMapper::mapToResource);
    }
}
