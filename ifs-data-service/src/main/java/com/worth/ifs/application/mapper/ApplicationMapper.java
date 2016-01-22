package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.finance.mapper.ApplicationFinanceMapper;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
        ApplicationFinanceMapper.class,
        ApplicationStatusMapper.class,
        CompetitionMapper.class
    }
)
public abstract class ApplicationMapper {

    @Autowired
    private ApplicationRepository repository;

    public abstract ApplicationResource mapApplicationToResource(Application object);

    public abstract Application mapResourceToApplication(ApplicationResource resource);

    public Long mapApplicationToId(Application object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Application mapIdToApplication(Long id) {
        return repository.findOne(id);
    }

    public Long mapApplicationResourceToId(ApplicationResource object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}