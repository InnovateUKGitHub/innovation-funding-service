package com.worth.ifs.finance.security;

import com.worth.ifs.finance.mapper.FinanceRowMetaFieldMapper;
import com.worth.ifs.finance.repository.FinanceRowMetaFieldRepository;
import com.worth.ifs.finance.resource.FinanceRowMetaFieldResource;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import com.worth.ifs.finance.domain.FinanceRowMetaField;
import org.springframework.stereotype.Component;

/**
 * Lookup strategies for {@link FinanceRowMetaFieldResource} and {@link FinanceRowMetaField} for permissioning
 */
@Component
@PermissionEntityLookupStrategies
public class FinanceRowMetaFieldLookupStrategy {


    @Autowired
    private FinanceRowMetaFieldRepository financeRowMetaFieldRepository;

    @Autowired
    private FinanceRowMetaFieldMapper mapper;

    @PermissionEntityLookupStrategy
    public FinanceRowMetaFieldResource getCostField(final Long costFieldId) {
        return mapper.mapToResource(financeRowMetaFieldRepository.findOne(costFieldId));
    }
}
