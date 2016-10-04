package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.domain.*;
import com.worth.ifs.project.finance.mapper.CostCategoryMapper;
import com.worth.ifs.project.finance.repository.FinanceCheckRepository;
import com.worth.ifs.project.finance.resource.*;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.util.Collections.emptyList;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

/**
 * A transactional service for finance check functionality
 */
@Service
public class FinanceCheckServiceImpl implements FinanceCheckService {

    @Autowired
    private FinanceCheckRepository financeCheckRepository;

    @Override
    public ServiceResult<FinanceCheckResource> getByProjectAndOrganisation(ProjectOrganisationCompositeId key) {
        return find(financeCheckRepository.findByProjectIdAndOrganisationId(key.getProjectId(), key.getOrganisationId()),
                notFoundError(FinanceCheck.class, id)).
                andOnSuccessReturn(this::mapToResource);
    }

    @Override
    public ServiceResult<Void> save(FinanceCheckResource financeCheckResource) {
        FinanceCheck toSave = mapToDomain(financeCheckResource);
        financeCheckRepository.save(toSave);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<FinanceCheckResource> generate(ProjectOrganisationCompositeId key) {
        throw new UnsupportedOperationException();
    }

    private FinanceCheck mapToDomain(FinanceCheckResource financeCheckResource){
        FinanceCheck fc = financeCheckRepository.findByProjectIdAndOrganisationId(financeCheckResource.getProject(), financeCheckResource.getOrganisation());
        for(CostResource cr : financeCheckResource.getCostGroup().getCosts()){
            Optional<Cost> oc = fc.getCostGroup().getCostById(cr.getId());
            if(oc.isPresent()){
                Cost c = oc.get();
                c.setValue(cr.getValue());
            }
        }
        return fc;
    }

    private FinanceCheckResource mapToResource(FinanceCheck fc) {
        Map<Object, Object> ctx  = new HashMap<>();
        FinanceCheckResource financeCheckResource = new FinanceCheckResource();
        financeCheckResource.setId(fc.getId());
        financeCheckResource.setOrganisation(fc.getOrganisation().getId());
        financeCheckResource.setProject(fc.getProject().getId());
        financeCheckResource.setCostGroup(mapCostGroupToResource(fc.getCostGroup(), ctx));
        return financeCheckResource;
    }



    private CostGroupResource mapCostGroupToResource(CostGroup cg, Map<Object, Object> ctx){
        return resource(ctx, cg, CostGroupResource::new, cgr -> {
            cgr.setId(cg.getId());
            cgr.setDescription(cg.getDescription());
            List<CostResource> costResources = simpleMap(cg.getCosts(), c -> mapCostsToCostResource(c, ctx));
            cgr.setCosts(costResources);
        });
    }

    private CostResource mapCostsToCostResource(Cost c, Map<Object, Object> ctx){
            return resource(ctx, c, CostResource::new, cr -> {
                cr.setId(c.getId());
                cr.setValue(c.getValue());
                CostCategoryResource costCategoryResource = mapCostCategoryToCostCategoryResource(c.getCostCategory(), ctx);
                cr.setCostCategory(costCategoryResource);
        });
    }

    private CostCategoryResource mapCostCategoryToCostCategoryResource(CostCategory cc, Map<Object, Object> ctx){
        return resource(ctx, cc, CostCategoryResource::new,  ccr -> {
                ccr.setLabel(cc.getLabel());
                ccr.setId(cc.getId());
                CostCategoryGroupResource costCategoryGroupResource = mapCostCategoryGroupToCostCategoryGroupResource(cc.getCostCategoryGroup(), ctx);
                ccr.setCostCategoryGroup(costCategoryGroupResource);
        });

    }

    private CostCategoryGroupResource mapCostCategoryGroupToCostCategoryGroupResource(CostCategoryGroup ccg, Map<Object, Object> ctx){
        return resource(ctx, ccg, CostCategoryGroupResource::new, ccgr -> {
            ccgr.setId(ccg.getId());
            List<CostCategoryResource> costCategoryResources = simpleMap(ccg.getCostCategories(), cc -> mapCostCategoryToCostCategoryResource(cc, ctx));
            ccgr.setCostCategories(costCategoryResources);
            ccgr.setDescription(ccg.getDescription());
        });
    }

    private <T> T resource(Map<Object, Object> ctx, Object domain, Supplier<T> constructor, Consumer<T> populator){
        if (domain == null){
            return null;
        }
        if (!ctx.containsKey(domain)){
            T resource = constructor.get();
            ctx.put(domain, resource);
            populator.accept(resource);
        }
        return (T)ctx.get(domain);
    }

}
