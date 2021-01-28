package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.procurement.milestone.domain.ProjectProcurementMilestone;
import org.innovateuk.ifs.procurement.milestone.repository.ProcurementMilestoneRepository;
import org.innovateuk.ifs.procurement.milestone.repository.ProjectProcurementMilestoneRepository;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneId;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectProcurementMilestoneServiceImpl
        extends AbstractProcurementMilestoneServiceImpl<ProjectProcurementMilestoneResource, ProjectProcurementMilestone, ProjectProcurementMilestoneId>
        implements ProjectProcurementMilestoneService {

    @Autowired
    private ProjectProcurementMilestoneRepository repository;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Override
    protected ServiceResult<ProjectProcurementMilestone> newDomain(ProjectProcurementMilestoneResource resource) {
        return find(projectFinanceRepository.findByProjectIdAndOrganisationId(resource.getProjectId(), resource.getOrganisationId()),
                notFoundError(ProjectFinance.class, resource.getProjectId(), resource.getOrganisationId()))
                .andOnSuccessReturn(applicationFinance -> {
                    ProjectProcurementMilestone domain = new ProjectProcurementMilestone();
                    domain.setProjectFinance(applicationFinance);
                    return domain;
                });
    }


    @Override
    protected ProcurementMilestoneRepository<ProjectProcurementMilestone> getRepository() {
        return repository;
    }

    @Override
    public ServiceResult<List<ProjectProcurementMilestoneResource>> getByProjectIdAndOrganisationId(long projectId, long organisationId) {
        return serviceSuccess(repository.findByProjectFinanceProjectIdAndProjectFinanceOrganisationIdOrderByMonthAsc(projectId, organisationId)
                .stream()
                .map(mapper::mapToResource)
                .collect(toList()));
    }

    @Override
    public ServiceResult<List<ProjectProcurementMilestoneResource>> getByProjectId(long projectId) {
        return find(repository.findByProjectFinanceProjectId(projectId), notFoundError(ProjectProcurementMilestone.class, projectId))
                .andOnSuccessReturn((milestones) ->
                        milestones.stream()
                                .map(mapper::mapToResource)
                                .collect(toList()));
    }
}
