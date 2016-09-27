package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.domain.FinanceCheck;
import com.worth.ifs.project.finance.mapper.FinanceCheckMapper;
import com.worth.ifs.project.finance.repository.FinanceCheckRepository;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

/**
 * A service for finance check functionality
 */
@Service
public class FinanceCheckServiceImpl implements FinanceCheckService {

    @Autowired
    private FinanceCheckRepository financeCheckRepository;
    @Autowired
    private FinanceCheckMapper financeCheckMapper;

    @Override
    public ServiceResult<FinanceCheckResource> getById(Long id){
        return find(financeCheckRepository.findOne(id), notFoundError(FinanceCheck.class, id)).andOnSuccessReturn(financeCheckMapper::mapToResource);
    }

    @Override
    public ServiceResult<FinanceCheckResource> save(FinanceCheckResource toUpdate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServiceResult<FinanceCheckResource> generate(Long projectId) {
        throw new UnsupportedOperationException();
    }

}
