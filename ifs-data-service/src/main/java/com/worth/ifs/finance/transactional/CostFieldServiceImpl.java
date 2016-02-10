package com.worth.ifs.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.mapper.CostFieldMapper;
import com.worth.ifs.finance.repository.CostFieldRepository;
import com.worth.ifs.finance.resource.CostFieldResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class CostFieldServiceImpl implements CostFieldService {

    @Autowired
    private CostFieldRepository repository;

    @Autowired
    private CostFieldMapper costFieldMapper;

    @Override
    public ServiceResult<CostField> findOne(Long id) {
        return find(() -> repository.findOne(id), notFoundError(CostField.class, id));
    }

    @Override
    public ServiceResult<List<CostFieldResource>> findAll() {
        List<CostField> allCostFields = repository.findAll();
        List<CostFieldResource> resources = simpleMap(allCostFields, costFieldMapper::mapCostFieldToResource);
        return serviceSuccess(resources);
    }
}