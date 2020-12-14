package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.domain.ProcurementMilestone;
import org.innovateuk.ifs.procurement.milestone.mapper.ProcurementMilestoneMapper;
import org.innovateuk.ifs.procurement.milestone.repository.ProcurementMilestoneRepository;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

public abstract class AbstractProcurementMilestoneServiceImpl<R extends ProcurementMilestoneResource, D extends ProcurementMilestone> implements ProcurementMilestoneService<R> {

    @Autowired
    protected ProcurementMilestoneMapper<D, R> mapper;

    public ServiceResult<R> create(R resource) {
        return newDomain(resource).andOnSuccessReturn(domain -> {
            updateFromResource(domain, resource);
            return mapper.mapToResource(getRepository().save(domain));
        });
    }

    public ServiceResult<R> update(R resource) {
        return findById(resource.getId()).andOnSuccessReturn((domain) -> {
            updateFromResource(domain, resource);
            return mapper.mapToResource(getRepository().save(domain));
        });
    }

    public ServiceResult<Void> delete(long milestoneId) {
        getRepository().deleteById(milestoneId);
        return serviceSuccess();
    }

    public ServiceResult<R> get(long milestoneId) {
        return findById(milestoneId).andOnSuccessReturn(mapper::mapToResource);
    }

    private ServiceResult<D> findById(long milestoneId) {
        return find(getRepository().findById(milestoneId), notFoundError(ProcurementMilestone.class, milestoneId));
    }

    private void updateFromResource(D domain, R resource) {
        domain.setMonth(resource.getMonth());
        domain.setDescription(resource.getDescription());
        domain.setTaskOrActivity(resource.getTaskOrActivity());
        domain.setDeliverable(resource.getDeliverable());
        domain.setSuccessCriteria(resource.getSuccessCriteria());
        domain.setPayment(resource.getPayment());
    }

    protected abstract ServiceResult<D> newDomain(R resource);
    protected abstract ProcurementMilestoneRepository<D> getRepository();

}
