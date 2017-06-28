package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.finance.mapper.FinanceRowMetaFieldMapper;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaFieldRepository;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
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
