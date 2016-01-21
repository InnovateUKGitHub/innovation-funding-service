package com.worth.ifs.finance.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.domain.CostValue;
import com.worth.ifs.finance.domain.CostValueId;
import com.worth.ifs.finance.repository.CostValueRepository;
import com.worth.ifs.finance.resource.CostValueResource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        CostMapper.class,
        CostField.class,
        CostValueIdMapper.class
    }
)
public abstract class CostValueMapper {

    @Autowired
    private CostValueRepository repository;

    public abstract CostValueResource mapCostValueToResource(CostValue object);

    public abstract CostValue resourceToCostValue(CostValueResource resource);

    public CostValueId mapCostValueToId(CostValue object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public CostValue mapIdToCostValue(CostValueId id) {
        return repository.findOne(id);
    }

    public CostValueId mapCostValueResourceToId(CostValueResource object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}