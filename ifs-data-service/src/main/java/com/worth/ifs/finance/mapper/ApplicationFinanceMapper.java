package com.worth.ifs.finance.mapper;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.user.mapper.OrganisationMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        OrganisationMapper.class,
        ApplicationMapper.class
    }
)
public abstract class ApplicationFinanceMapper {

    @Autowired
    private ApplicationFinanceRepository repository;

    public abstract ApplicationFinanceResource mapApplicationFinanceToResource(ApplicationFinance object);

    public abstract ApplicationFinance resourceToApplicationFinance(ApplicationFinanceResource resource);

    public Long mapApplicationFinanceToId(ApplicationFinance object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public ApplicationFinance mapIdToApplicationFinance(Long id) {
        return repository.findOne(id);
    }

    public Long mapApplicationFinanceResourceToId(ApplicationFinanceResource object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}