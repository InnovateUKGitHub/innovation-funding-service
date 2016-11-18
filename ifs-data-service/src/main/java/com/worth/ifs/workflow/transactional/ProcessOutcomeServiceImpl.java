package com.worth.ifs.workflow.transactional;

import com.worth.ifs.assessment.repository.ProcessOutcomeRepository;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.mapper.ProcessOutcomeMapper;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

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
        return find(repository.findTopByProcessIdAndOutcomeTypeOrderByIdDesc(processId, outcomeType), notFoundError(ProcessOutcome.class, processId)).andOnSuccessReturn(processOutcomeMapper::mapToResource);
    }
}