package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.procurement.milestone.domain.ApplicationProcurementMilestone;
import org.innovateuk.ifs.procurement.milestone.repository.ApplicationProcurementMilestoneRepository;
import org.innovateuk.ifs.procurement.milestone.repository.ProcurementMilestoneRepository;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationProcurementMilestoneServiceImpl
        extends AbstractProcurementMilestoneServiceImpl<ApplicationProcurementMilestoneResource, ApplicationProcurementMilestone>
        implements ApplicationProcurementMilestoneService {

    @Autowired
    private ApplicationProcurementMilestoneRepository repository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Override
    protected ServiceResult<ApplicationProcurementMilestone> newDomain(ApplicationProcurementMilestoneResource resource) {
        return find(applicationFinanceRepository.findByApplicationIdAndOrganisationId(resource.getApplicationId(), resource.getOrganisationId()),
                notFoundError(ApplicationFinance.class, resource.getApplicationId(), resource.getOrganisationId()))
                .andOnSuccessReturn(applicationFinance -> {
                    ApplicationProcurementMilestone domain = new ApplicationProcurementMilestone();
                    domain.setApplicationFinance(applicationFinance);
                    return domain;
                });
    }

    @Override
    protected ProcurementMilestoneRepository<ApplicationProcurementMilestone> getRepository() {
        return repository;
    }

    @Override
    public ServiceResult<List<ApplicationProcurementMilestoneResource>> getByApplicationIdAndOrganisationId(long applicationId, long organisationId) {
        return find(repository.findByApplicationFinanceApplicationIdAndApplicationFinanceOrganisationId(applicationId, organisationId), notFoundError(ApplicationProcurementMilestone.class, applicationId, organisationId))
                .andOnSuccessReturn((milestones) ->
                        milestones.stream()
                                .map(mapper::mapToResource)
                                .collect(toList()));
    }
}
