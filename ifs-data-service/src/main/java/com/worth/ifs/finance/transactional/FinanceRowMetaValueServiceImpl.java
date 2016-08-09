package com.worth.ifs.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.FinanceRowMetaValue;
import com.worth.ifs.finance.resource.FinanceRowMetaValueId;
import com.worth.ifs.finance.mapper.FinanceRowMetaValueMapper;
import com.worth.ifs.finance.repository.FinanceRowMetaValueRepository;
import com.worth.ifs.finance.resource.FinanceRowMetaValueResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class FinanceRowMetaValueServiceImpl extends BaseTransactionalService implements FinanceRowMetaValueService {

    @Autowired
    private FinanceRowMetaValueRepository repository;

    @Autowired
    private FinanceRowMetaValueMapper mapper;

    @Override
    public ServiceResult<FinanceRowMetaValueResource> findOne(FinanceRowMetaValueId id) {
        return find(repository.findOne(id), notFoundError(FinanceRowMetaValue.class)).andOnSuccessReturn(mapper::mapToResource);
    }
}