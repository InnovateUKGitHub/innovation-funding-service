package com.worth.ifs.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.CostValue;
import com.worth.ifs.finance.resource.CostValueId;
import com.worth.ifs.finance.mapper.CostValueMapper;
import com.worth.ifs.finance.repository.CostValueRepository;
import com.worth.ifs.finance.resource.CostValueResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class CostValueServiceImpl extends BaseTransactionalService implements CostValueService {

    @Autowired
    private CostValueRepository repository;

    @Autowired
    private CostValueMapper mapper;

    @Override
    public ServiceResult<CostValueResource> findOne(CostValueId id) {
        return find(repository.findOne(id), notFoundError(CostValue.class)).andOnSuccessReturn(mapper::mapToResource);
    }
}