package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.CostTotal;
import org.innovateuk.ifs.finance.mapper.CostTotalMapper;
import org.innovateuk.ifs.finance.repository.CostTotalRepository;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Service concerned with the {@link CostTotal} domain.
 */
@Transactional(readOnly = true)
@Service
public class CostTotalService {

    private CostTotalRepository costTotalRepository;
    private CostTotalMapper costTotalMapper;

    @Autowired
    public CostTotalService(
            CostTotalRepository costTotalRepository,
            CostTotalMapper costTotalMapper
    ) {
        this.costTotalRepository = costTotalRepository;
        this.costTotalMapper = costTotalMapper;
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @Transactional
    public ServiceResult<Void> saveCostTotal(FinanceCostTotalResource costTotalResource) {
        CostTotal costTotal = costTotalMapper.mapToDomain(costTotalResource);
        costTotalRepository.save(costTotal);

        return serviceSuccess();
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @Transactional
    public ServiceResult<Void> saveCostTotals(Collection<FinanceCostTotalResource> costTotalResources) {
        return ServiceResult.processAnyFailuresOrSucceed(simpleMap(costTotalResources, this::saveCostTotal));
    }
}
