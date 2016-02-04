package com.worth.ifs.workflow.mapper;

import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import com.worth.ifs.workflow.transactional.ProcessOutcomeService;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    componentModel = "spring",
    uses = {

    }
)
public abstract class ProcessOutcomeMapper {

    @Autowired
    private ProcessOutcomeService service;

    public abstract ProcessOutcomeResource mapProcessOutcomeToResource(ProcessOutcome object);

    public abstract ProcessOutcome resourceToProcessOutcome(ProcessOutcomeResource resource);

    public Long mapProcessOutcomeToId(ProcessOutcome object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public ProcessOutcome mapIdToProcessOutcome(Long id) {
        return service.findOne(id);
    }
}