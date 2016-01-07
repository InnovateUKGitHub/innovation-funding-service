package com.worth.ifs.finance.mapper;

import com.worth.ifs.application.mapper.QuestionMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.CostResource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CostValueMapper.class,
        ApplicationFinanceMapper.class,
        QuestionMapper.class
    }
)
public abstract class CostMapper {

    @Autowired
    private CostRepository repository;

    public abstract CostResource mapCostToResource(Cost object);

    public abstract Cost resourceToCost(CostResource resource);

    public Long mapCostToId(Cost object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Cost mapIdToCost(Long id) {
        return repository.findOne(id);
    }

    public Long mapCostResourceToId(CostResource object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}