package com.worth.ifs.finance.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.repository.CostFieldRepository;
import com.worth.ifs.finance.resource.CostFieldResource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class
)
public abstract class CostFieldMapper {

    @Autowired
    private CostFieldRepository repository;

    public abstract CostFieldResource mapCostFieldToResource(CostField object);

    public abstract CostField resourceToCostField(CostFieldResource resource);

    public Long mapCostFieldToId(CostField object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public CostField mapIdToCostField(Long id) {
        return repository.findOne(id);
    }

    public Long mapCostFieldResourceToId(CostFieldResource object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}