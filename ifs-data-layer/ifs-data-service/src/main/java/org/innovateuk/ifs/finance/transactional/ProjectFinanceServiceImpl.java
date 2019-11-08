package org.innovateuk.ifs.finance.transactional;

import org.checkerframework.checker.units.qual.A;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.EmployeesAndTurnover;
import org.innovateuk.ifs.finance.domain.GrowthTable;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.handler.OrganisationTypeFinanceHandler;
import org.innovateuk.ifs.finance.handler.ProjectFinanceHandler;
import org.innovateuk.ifs.finance.mapper.ProjectFinanceMapper;
import org.innovateuk.ifs.finance.repository.EmployeesAndTurnoverRepository;
import org.innovateuk.ifs.finance.repository.GrowthTableRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResourceId;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectFinanceServiceImpl extends AbstractFinanceService<ProjectFinance, ProjectFinanceResource> implements ProjectFinanceService {

    @Autowired
    private EmployeesAndTurnoverRepository employeesAndTurnoverRepository;

    @Autowired
    private GrowthTableRepository growthTableRepository;

    @Autowired
    private ProjectFinanceHandler projectFinanceHandler;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private OrganisationFinanceDelegate organisationFinanceDelegate;

    @Override
    public ServiceResult<ProjectFinanceResource> financeChecksDetails(long projectId, long organisationId) {
        return getProjectFinanceForOrganisation(new ProjectFinanceResourceId(projectId, organisationId));
    }

    @Override
    public ServiceResult<List<ProjectFinanceResource>> financeChecksTotals(long projectId) {
        return find(projectFinanceHandler.getFinanceChecksTotals(projectId), notFoundError(ProjectFinance.class, projectId));
    }
    @Override
    public ServiceResult<Void> createProjectFinance(long projectId, long organisationId) {
        return find(project(projectId), organisation(organisationId)).andOnSuccessReturnVoid((project, organisation) -> {
            ProjectFinance projectFinance = projectFinanceRepository.save(new ProjectFinance(project, organisation));
            if (TRUE.equals(projectFinance.getCompetition().getIncludeProjectGrowthTable())) {
                projectFinance.setGrowthTable(new GrowthTable());
                growthTableRepository.save(projectFinance.getGrowthTable());
            } else {
                projectFinance.setEmployeesAndTurnover(new EmployeesAndTurnover());
                employeesAndTurnoverRepository.save(projectFinance.getEmployeesAndTurnover());
            }
            OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getCompetition().getId(), projectFinance.getOrganisation().getOrganisationType().getId());

            for (FinanceRowType costType : projectFinance.getCompetition().getFinanceRowTypes()) {
                organisationFinanceHandler.initialiseCostType(projectFinance, costType);
            }
        });
    }


    @Override
    public ServiceResult<Void> updateProjectFinance(long projectFinanceId, ProjectFinanceResource projectFinanceResource) {
        return find(projectFinanceRepository.findById(projectFinanceId), notFoundError(ProjectFinance.class, projectFinanceId)).andOnSuccess(dbFinance -> {
            updateFinanceDetails(dbFinance, projectFinanceResource);
            return serviceSuccess();
        });
    }

    private ServiceResult<ProjectFinanceResource> getProjectFinanceForOrganisation(ProjectFinanceResourceId projectFinanceResourceId) {
        return projectFinanceHandler.getProjectOrganisationFinances(projectFinanceResourceId);
    }

}
