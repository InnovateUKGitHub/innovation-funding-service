package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.domain.ProcurementMilestone;
import org.innovateuk.ifs.procurement.milestone.mapper.ProcurementMilestoneMapper;
import org.innovateuk.ifs.procurement.milestone.repository.ProcurementMilestoneRepository;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneId;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

public abstract class AbstractProcurementMilestoneServiceImpl<R extends ProcurementMilestoneResource, D extends ProcurementMilestone, S extends ProcurementMilestoneId>
        extends RootTransactionalService
        implements ProcurementMilestoneService<R, S> {

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

    public ServiceResult<Void> delete(ProcurementMilestoneId milestoneId) {
        getRepository().deleteById(milestoneId.getId());
        return serviceSuccess();
    }

    public ServiceResult<R> get(ProcurementMilestoneId milestoneId) {
        return findById(milestoneId.getId()).andOnSuccessReturn(mapper::mapToResource);
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
