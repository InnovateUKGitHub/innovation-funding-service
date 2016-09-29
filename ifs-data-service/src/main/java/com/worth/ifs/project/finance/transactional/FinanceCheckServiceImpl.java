package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.domain.FinanceCheck;
import com.worth.ifs.project.finance.mapper.FinanceCheckMapper;
import com.worth.ifs.project.finance.repository.FinanceCheckRepository;
import com.worth.ifs.project.finance.resource.CostGroupResource;
import com.worth.ifs.project.finance.resource.CostResource;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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
    public ServiceResult<FinanceCheckResource> getByProjectAndOrganisation(ProjectOrganisationCompositeId key){
        return find(financeCheckRepository.findByProjectIdAndOrganisationId(key.getProjectId(), key.getOrganisationId()), notFoundError(FinanceCheck.class, id)).
                andOnSuccessReturn(fc -> {
                    FinanceCheckResource financeCheckResource = new FinanceCheckResource();
                    financeCheckResource.setId(fc.getId());
                    financeCheckResource.setOrganisation(fc.getOrganisation().getId());
                    financeCheckResource.setProject(fc.getProject().getId());
                    CostGroupResource costGroupResource = new CostGroupResource();

                    if(fc.getCostGroup() != null) {
                        costGroupResource.setId(fc.getCostGroup().getId());
                        if(fc.getCostGroup().getCosts() != null) {
                            List<CostResource> costResourceList = simpleMap(fc.getCostGroup().getCosts(), c -> {
                                CostResource cr = new CostResource();
                                cr.setId(c.getId());
                                cr.setValue(c.getValue());
                                return cr;
                            });
                            costGroupResource.setCosts(costResourceList);
                            costGroupResource.setDescription(fc.getCostGroup().getDescription());
                            financeCheckResource.setCostGroup(costGroupResource);
                        }
                    }
                    return financeCheckResource;
                });
    }

    @Override
    public ServiceResult<FinanceCheckResource> save(FinanceCheckResource financeCheckResource) {
        FinanceCheck toSave = financeCheckMapper.mapToDomain(financeCheckResource);
        FinanceCheck saved = financeCheckRepository.save(toSave);
        return serviceSuccess(saved).andOnSuccessReturn(financeCheckMapper::mapToResource);

    }

    @Override
    public ServiceResult<FinanceCheckResource> generate(ProjectOrganisationCompositeId key) {
        throw new UnsupportedOperationException();
    }

}
