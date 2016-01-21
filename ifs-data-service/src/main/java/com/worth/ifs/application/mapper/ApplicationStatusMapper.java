package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.repository.ApplicationStatusRepository;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.finance.mapper.ApplicationFinanceMapper;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
        CompetitionMapper.class,
        ApplicationFinanceMapper.class
    }
)
public abstract class ApplicationStatusMapper {

    @Autowired
    private ApplicationStatusRepository repository;

    public abstract ApplicationStatusResource mapApplicationStatusToResource(ApplicationStatus object);

    public abstract ApplicationStatus resourceToApplicationStatus(ApplicationStatusResource resource);

    public Long mapApplicationStatusToId(ApplicationStatus object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public ApplicationStatus mapIdToApplicationStatus(Long id) {
        return repository.findOne(id);
    }
}