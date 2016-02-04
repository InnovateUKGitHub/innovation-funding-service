package com.worth.ifs.workflow.transactional;

import com.worth.ifs.assessment.repository.ProcessOutcomeRepository;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessOutcomeServiceImpl implements ProcessOutcomeService {
    @Autowired
    private ProcessOutcomeRepository repository;

    @Override
    public ProcessOutcome findOne(Long id) {
        return repository.findOne(id);
    }
}