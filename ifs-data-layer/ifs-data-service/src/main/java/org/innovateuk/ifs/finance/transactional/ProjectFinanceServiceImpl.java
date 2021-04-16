package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.handler.OrganisationTypeFinanceHandler;
import org.innovateuk.ifs.finance.handler.ProjectFinanceHandler;
import org.innovateuk.ifs.finance.mapper.ProjectFinanceMapper;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResourceId;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.projectteam.transactional.PendingPartnerProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.SUBSIDY_BASIS;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectFinanceServiceImpl extends AbstractFinanceService<ProjectFinance, ProjectFinanceResource> implements ProjectFinanceService {

    @Autowired
    private ProjectFinanceHandler projectFinanceHandler;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private OrganisationFinanceDelegate organisationFinanceDelegate;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ProjectFinanceMapper projectFinanceMapper;

    @Autowired
    private PendingPartnerProgressService pendingPartnerProgressService;

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
            initialiseFinancialYearData(projectFinance);

            OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(projectFinance.getCompetition().getId(), projectFinance.getOrganisation().getOrganisationType().getId());
            for (FinanceRowType costType : projectFinance.getCompetition().getFinanceRowTypes()) {
                organisationFinanceHandler.initialiseCostType(projectFinance, costType);
            }
        });
    }

    @Override
    @Transactional
    public ServiceResult<ProjectFinanceResource> updateProjectFinance(ProjectFinanceResource projectFinanceResource) {
        long projectFinanceId = projectFinanceResource.getId();
        return find(projectFinanceRepository.findById(projectFinanceId), notFoundError(ProjectFinance.class, projectFinanceId)).andOnSuccess(dbFinance -> {
            updateFinancialYearData(dbFinance, projectFinanceResource);
            if (projectFinanceResource.getNorthernIrelandDeclaration() != null) {
                if (!projectFinanceResource.getNorthernIrelandDeclaration().equals(dbFinance.getNorthernIrelandDeclaration())){
                    pendingPartnerProgressService.resetPendingPartnerProgress(SUBSIDY_BASIS, projectFinanceResource.getProject(), projectFinanceResource.getOrganisation());
                }
                dbFinance.setNorthernIrelandDeclaration(projectFinanceResource.getNorthernIrelandDeclaration());
            }
            return serviceSuccess(projectFinanceMapper.mapToResource(dbFinance));
        });
    }

    @Override
    public ServiceResult<Boolean> hasAnyProjectOrganisationSizeChangedFromApplication(long projectId) {

        return getProjectById(projectId).andOnSuccessReturn(project -> {

            List<ProjectFinance> projectFinances = projectFinanceRepository.findByProjectId(projectId).stream()
                    .filter(projectFinance -> projectFinance.getOrganisationSize() != null).collect(Collectors.toList());
            List<ApplicationFinance> applicationFinances = applicationFinanceRepository.findByApplicationId(project.getApplication().getId()).stream()
                    .filter(applicationFinance -> applicationFinance.getOrganisationSize() != null).collect(Collectors.toList());
            Map<Long, OrganisationSize> applicationOrganisationSizeMap = applicationFinances.stream().collect(
                    toMap(app -> app.getOrganisation().getId(), ApplicationFinance::getOrganisationSize));

            return projectFinances.stream()
                    .anyMatch(pf -> hasChanged(pf.getOrganisation(), pf.getOrganisationSize(), applicationOrganisationSizeMap));
        });

    }

    private boolean hasChanged(Organisation organisation, OrganisationSize projectOrgSize, Map<Long, OrganisationSize> organisationSizeMap) {
        if (!organisationSizeMap.containsKey(organisation.getId())) {
            // Organisations added at project stage will not have finances
            return false;
        }
        return !organisationSizeMap.get(organisation.getId()).equals(projectOrgSize);
    }

    @Override
    public ServiceResult<Double> getResearchParticipationPercentageFromProject(long projectId) {
        return getResearchPercentageFromProject(projectId).andOnSuccessReturn(BigDecimal::doubleValue);
    }

    private ServiceResult<BigDecimal> getResearchPercentageFromProject(Long projectId) {
        return find(projectFinanceHandler.getResearchParticipationPercentageFromProject(projectId), notFoundError(Project.class, projectId));
    }

    private ServiceResult<ProjectFinanceResource> getProjectFinanceForOrganisation(ProjectFinanceResourceId projectFinanceResourceId) {
        return projectFinanceHandler.getProjectOrganisationFinances(projectFinanceResourceId);
    }

    private ServiceResult<Project> getProjectById(long projectId) {
        return find(projectRepository.findById(projectId), notFoundError(Project.class, projectId));
    }

}