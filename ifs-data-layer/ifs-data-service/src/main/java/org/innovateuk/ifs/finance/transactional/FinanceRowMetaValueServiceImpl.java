package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.innovateuk.ifs.finance.mapper.FinanceRowMetaValueMapper;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaValueRepository;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaValueResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class FinanceRowMetaValueServiceImpl extends BaseTransactionalService implements FinanceRowMetaValueService {

    @Autowired
    private FinanceRowMetaValueRepository repository;

    @Autowired
    private FinanceRowMetaValueMapper mapper;

    @Override
    public ServiceResult<FinanceRowMetaValueResource> findOne(Long id) {
        return find(repository.findOne(id), notFoundError(FinanceRowMetaValue.class)).andOnSuccessReturn(mapper::mapToResource);
    }
}
