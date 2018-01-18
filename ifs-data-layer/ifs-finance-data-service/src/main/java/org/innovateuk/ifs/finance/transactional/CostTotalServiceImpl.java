package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.CostTotal;
import org.innovateuk.ifs.finance.mapper.CostTotalMapper;
import org.innovateuk.ifs.finance.repository.CostTotalRepository;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Transactional(readOnly = true)
@Service
public class CostTotalServiceImpl implements CostTotalService {

    @Autowired
    private CostTotalRepository costTotalRepository;

    @Autowired
    private CostTotalMapper costTotalMapper;

    @Transactional
    @Override
    public ServiceResult<Void> saveCostTotal(FinanceCostTotalResource costTotalResource) {
        CostTotal costTotal = costTotalMapper.mapToDomain(costTotalResource);
        costTotalRepository.save(costTotal);

        return serviceSuccess();
    }
}
