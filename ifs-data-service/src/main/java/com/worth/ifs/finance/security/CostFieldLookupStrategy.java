package com.worth.ifs.finance.security;

import com.worth.ifs.finance.mapper.CostFieldMapper;
import com.worth.ifs.finance.repository.CostFieldRepository;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.security.PermissionEntityLookupStrategies;
import com.worth.ifs.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import com.worth.ifs.finance.domain.CostField;
import org.springframework.stereotype.Component;

/**
 * Lookup strategies for {@link CostFieldResource} and {@link CostField} for permissioning
 */
@Component
@PermissionEntityLookupStrategies
public class CostFieldLookupStrategy {


    @Autowired
    private CostFieldRepository costFieldRepository;

    @Autowired
    private CostFieldMapper mapper;

    @PermissionEntityLookupStrategy
    public CostFieldResource getCostField(final Long costFieldId) {
        return mapper.mapToResource(costFieldRepository.findOne(costFieldId));
    }
}
